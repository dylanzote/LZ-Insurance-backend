package com.zote.notification.service.domain.support;

import com.zote.notification.service.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class TemplateSupport {

    public String generateTemplateId() {
        log.info("Generating new template ID");
        return "tmpl_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    private String generateTranslationId() {
        return "trans-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
    public NotificationTemplate buildNotificationTemplate(CreateTemplateData createTemplateData) {
        log.info("Building notification template for name: {}", createTemplateData.getName());

        return NotificationTemplate.builder()
            .id(generateTemplateId())
            .name(createTemplateData.getName())
            .description(createTemplateData.getDescription())
            .channel(createTemplateData.getChannel())
            .templatePath(createTemplateData.getTemplatePath())
            .subjectTemplate(createTemplateData.getSubjectTemplate())
            .bodyTemplate(createTemplateData.getBodyTemplate())
            .variablesSchema(createTemplateData.getVariablesSchema())
            .defaultLocale(createTemplateData.getDefaultLocale() != null ? createTemplateData.getDefaultLocale() : "en")
            .isActive(createTemplateData.getIsActive() != null ? createTemplateData.getIsActive() : true)
            .version(1)
            .build();
    }

    public void updateTemplate(NotificationTemplate template, UpdateTemplateData updateData) {
        log.info("Updating notification template: {}", template.getId());

        if (updateData.getName() != null) {
            template.setName(updateData.getName());
        }
        if (updateData.getDescription() != null) {
            template.setDescription(updateData.getDescription());
        }
        if (updateData.getTemplatePath() != null) {
            template.setTemplatePath(updateData.getTemplatePath());
        }
        if (updateData.getSubjectTemplate() != null) {
            template.setSubjectTemplate(updateData.getSubjectTemplate());
        }
        if (updateData.getBodyTemplate() != null) {
            template.setBodyTemplate(updateData.getBodyTemplate());
        }
        if (updateData.getVariablesSchema() != null) {
            template.setVariablesSchema(updateData.getVariablesSchema());
        }
        if (updateData.getIsActive() != null) {
            template.setIsActive(updateData.getIsActive());
        }

        template.setVersion(template.getVersion() + 1);
    }

    public TemplateTranslation buildTemplateTranslation(AddTranslationData addTranslationData) {
        log.info("Building template translation for template ID: {} and locale: {}", addTranslationData.getTemplateId(), addTranslationData.getLocale());

        return TemplateTranslation.builder()
            .id(generateTranslationId())
            .templateId(addTranslationData.getTemplateId())
            .locale(addTranslationData.getLocale())
            .subject(addTranslationData.getSubject())
            .body(addTranslationData.getBody())
            .build();
    }

    public void updateTemplateTranslation(TemplateTranslation translation, UpdateTranslationData updateData) {
        log.info("Updating template translation: {} for locale: {}", translation.getId(), translation.getLocale());

        if (updateData.getSubject() != null) {
            translation.setSubject(updateData.getSubject());
        }
        if (updateData.getBody() != null) {
            translation.setBody(updateData.getBody());
        }
    }
}
