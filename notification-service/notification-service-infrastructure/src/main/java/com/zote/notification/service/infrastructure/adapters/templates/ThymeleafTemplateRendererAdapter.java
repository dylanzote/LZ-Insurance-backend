package com.zote.notification.service.infrastructure.adapters.templates;

import com.zote.common.utils.exceptions.InvalidTemplateVariableException;
import com.zote.common.utils.exceptions.MissingTemplateVariableException;
import com.zote.notification.service.domain.model.NotificationTemplate;
import com.zote.notification.service.domain.model.TemplateTranslation;
import com.zote.notification.service.domain.model.TemplateVariable;
import com.zote.notification.service.domain.ports.outbound.repository.TemplateRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.TemplateRendererPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ThymeleafTemplateRendererAdapter implements TemplateRendererPort {

    private final TemplateEngine templateEngine;

    private final TemplateRepositoryPort templateRepository;

    @Override
    public String renderTemplate(String templateId, Map<String, Object> variables, String locale) {
        NotificationTemplate template = templateRepository.findById(templateId);
        String bodyTemplate = getLocalizedContent(template, locale, "body");
        return renderInlineTemplate(bodyTemplate, variables, locale);
    }

    @Override
    public String renderSubject(String templateId, Map<String, Object> variables, String locale) {
        NotificationTemplate template = templateRepository.findById(templateId);
        String subjectTemplate = getLocalizedContent(template, locale, "subject");
        return renderInlineTemplate(subjectTemplate, variables, locale);
    }

    @Override
    public String renderHtml(String templateId, Map<String, Object> variables, String locale) {
        log.debug("Rendering HTML template: {} with locale: {}", templateId, locale);
        
        // Load template metadata from database
        NotificationTemplate template = templateRepository.findById(templateId);
        String templatePath = template.getTemplatePath();
        
        if (templatePath == null || templatePath.isEmpty()) {
            log.warn("No template path defined for template: {}", templateId);
            return template.getBodyTemplate(); // Fallback to plain text
        }

        log.debug("Loading HTML template from classpath: {}", templatePath);
        
        // Render HTML from filesystem using Thymeleaf classpath loader
        return renderFilesystemTemplate(templatePath, variables, locale);
    }

    @Override
    public void validateVariables(String templateId, Map<String, Object> variables) {
        NotificationTemplate template = templateRepository.findById(templateId);

        Map<String, TemplateVariable> schema = template.getVariablesSchema();
        if (schema == null || schema.isEmpty()) {
            return; // No validation required
        }

        // Validate required variables
        schema.entrySet().stream()
                .filter(entry -> Boolean.TRUE.equals(entry.getValue().getRequired()))
                .forEach(entry -> {
                    if (!variables.containsKey(entry.getKey()) || variables.get(entry.getKey()) == null) {
                        throw new MissingTemplateVariableException("Required variable missing: " + entry.getKey());
                    }
                });

        // Validate variable types and constraints
        variables.forEach((key, value) -> {
            if (schema.containsKey(key)) {
                validateVariable(key, value, schema.get(key));
            }
        });
    }

    public static class FormatDateFunction {
        public String format(LocalDate date, String pattern) {
            if (date == null) return "";
            return date.format(DateTimeFormatter.ofPattern(pattern));
        }

        public String format(LocalDateTime dateTime, String pattern) {
            if (dateTime == null) return "";
            return dateTime.format(DateTimeFormatter.ofPattern(pattern));
        }

        public String format(Date date, String pattern) {
            if (date == null) return "";
            return new SimpleDateFormat(pattern).format(date);
        }
    }

    public static class FormatCurrencyFunction {
        public String format(Double amount, String currencyCode, Locale locale) {
            if (amount == null) return "";
            Currency currency = Currency.getInstance(currencyCode);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
            symbols.setCurrencySymbol(currency.getSymbol(locale));
            DecimalFormat format = new DecimalFormat("#,##0.00", symbols);
            format.setCurrency(currency);
            return format.format(amount);
        }
    }

    public static class TruncateFunction {
        public String truncate(String text, int maxLength) {
            if (text == null) return "";
            if (text.length() <= maxLength) return text;
            return text.substring(0, maxLength - 3) + "...";
        }

        public String truncate(String text, int maxLength, String suffix) {
            if (text == null) return "";
            if (text.length() <= maxLength) return text;
            return text.substring(0, maxLength - suffix.length()) + suffix;
        }
    }

    public static class MaskEmailFunction {
        public String mask(String email) {
            if (email == null || !email.contains("@")) return email;
            String[] parts = email.split("@");
            String localPart = parts[0];
            String domain = parts[1];

            if (localPart.length() <= 2) return "***@" + domain;

            return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + domain;
        }
    }

    public static class MaskPhoneFunction {
        public String mask(String phone) {
            if (phone == null || phone.length() < 4) return phone;
            return phone.substring(0, phone.length() - 4) + "****";
        }
    }

    private void validateVariable(String key, Object value, TemplateVariable schema) {
        if (schema.getType() == null) return;

        switch (schema.getType()) {
            case STRING:
                if (!(value instanceof String strValue)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be a string");
                }
                if (schema.getMaxLength() != null && strValue.length() > schema.getMaxLength()) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " exceeds max length of " + schema.getMaxLength());
                }
                if (schema.getPattern() != null && !strValue.matches(schema.getPattern())) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " does not match pattern: " + schema.getPattern());
                }
                break;

            case NUMBER:
                if (!(value instanceof Number)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be a number");
                }
                break;

            case BOOLEAN:
                if (!(value instanceof Boolean)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be a boolean");
                }
                break;

            case DATE:
                if (!(value instanceof LocalDate)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be a LocalDate");
                }
                break;

            case DATETIME:
                if (!(value instanceof LocalDateTime)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be a LocalDateTime");
                }
                break;
            case ARRAY:
                if (!(value instanceof List)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be an array (List)");
                }
                break;
            case OBJECT:
                if (!(value instanceof Map)) {
                    throw new InvalidTemplateVariableException(
                            "Variable " + key + " must be an object (Map)");
                }
                break;
        }

        // Validate enum values
        if (schema.getEnumValues() != null && !schema.getEnumValues().isEmpty()) {
            String stringValue = value.toString();
            if (!schema.getEnumValues().contains(stringValue)) {
                throw new InvalidTemplateVariableException(
                        "Variable " + key + " must be one of: " + String.join(", ", schema.getEnumValues()));
            }
        }
    }

    /**
     * Renders a filesystem-based HTML template from classpath.
     * Template path example: "email/welcome-customer" will load "templates/email/welcome-customer.html"
     */
    private String renderFilesystemTemplate(String templatePath, Map<String, Object> variables, String locale) {
        Context context = createThymeleafContext(variables, locale);
        
        try {
            // Thymeleaf will load from: src/main/resources/templates/{templatePath}.html
            return templateEngine.process(templatePath, context);
        } catch (Exception e) {
            log.error("Failed to render filesystem template: {}", templatePath, e);
            throw new IllegalStateException("Template rendering failed: " + templatePath, e);
        }
    }

    /**
     * Renders an inline template string (for subjects, plain text bodies, etc.)
     * Uses simple variable replacement, not Thymeleaf template processing
     */
    private String renderInlineTemplate(String templateContent, Map<String, Object> variables, String locale) {
        if (templateContent == null || templateContent.trim().isEmpty()) {
            return "";
        }

        String result = templateContent;
        
        // Simple variable replacement: {{variableName}} -> actual value
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String placeholder = "{{" + entry.getKey() + "}}";
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace(placeholder, value);
            }
        }
        
        return result;
    }

    /**
     * Creates a Thymeleaf context with variables and helper functions
     * i18n messages are automatically loaded by Spring MessageSource
     */
    private Context createThymeleafContext(Map<String, Object> variables, String locale) {
        Context context = new Context(Locale.forLanguageTag(locale));

        // Add all variables to context
        if (variables != null) {
            variables.forEach(context::setVariable);
        }

        // Add helper functions
        addHelperFunctions(context);

        return context;
    }

    private String getLocalizedContent(NotificationTemplate template, String locale, String type) {
        // Try to get translation
        TemplateTranslation translation = templateRepository.findTranslation(template.getId(), locale);

        if (Objects.nonNull(translation)) {
            return getTranslation(translation, type);
        }

        // Fallback to default locale
        if (template.getDefaultLocale() != null && !template.getDefaultLocale().equals(locale)) {
            TemplateTranslation defaultTranslation = templateRepository
                    .findTranslation(template.getId(), template.getDefaultLocale());

            if (Objects.nonNull(defaultTranslation)) {
                return getTranslation(defaultTranslation, type);
            }
        }

        // Fallback to template's original content
        return switch (type) {
            case "subject" -> template.getSubjectTemplate();
            case "body" -> template.getBodyTemplate();
            default -> throw new IllegalArgumentException("Invalid template type: " + type);
        };
    }

    private void addHelperFunctions(Context context) {
        Map<String, Object> helpers = new HashMap<>();
        helpers.put("formatDate", new FormatDateFunction());
        helpers.put("formatCurrency", new FormatCurrencyFunction());
        helpers.put("truncate", new TruncateFunction());
        helpers.put("maskEmail", new MaskEmailFunction());
        helpers.put("maskPhone", new MaskPhoneFunction());

        context.setVariable("helpers", helpers);
    }


    private String getTranslation(TemplateTranslation translation, String type) {
        return switch (type) {
            case "subject" -> translation.getSubject();
            case "body" -> translation.getBody();
            default -> throw new IllegalArgumentException("Invalid template type: " + type);
        };
    }

}
