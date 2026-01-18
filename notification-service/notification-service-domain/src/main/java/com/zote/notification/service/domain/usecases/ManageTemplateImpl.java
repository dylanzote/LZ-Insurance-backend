package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.exceptions.TemplateNotFoundException;
import com.zote.notification.service.domain.model.*;
import com.zote.notification.service.domain.ports.inbound.ManageTemplatesPort;
import com.zote.notification.service.domain.ports.outbound.repository.TemplateRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.TemplateRendererPort;
import com.zote.notification.service.domain.support.TemplateSupport;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManageTemplateImpl implements ManageTemplatesPort {

    private final TemplateRepositoryPort templateRepository;
    private final TemplateRendererPort templateRenderer;
    private final TemplateSupport templateSupport;

    @Override
    public NotificationTemplate createTemplate(CreateTemplateData createTemplateData) {
        log.info("Creating template: {}", createTemplateData.getName());
        templateRepository.findByName(createTemplateData.getName())
                .ifPresent(existing -> {
                    throw new FunctionalError("Template with name already exists: " + createTemplateData.getName());
                });
        var template = templateSupport.buildNotificationTemplate(createTemplateData);
        return templateRepository.save(template);
    }

    @Override
    public NotificationTemplate updateTemplate(UpdateTemplateData updateTemplateData) {
        log.info("Updating template: {}", updateTemplateData.getTemplateId());
        var template = templateRepository.findById(updateTemplateData.getTemplateId());
        templateSupport.updateTemplate(template, updateTemplateData);
        return templateRepository.save(template);
    }

    @Override
    public void deleteTemplate(String templateId) {
        log.info("Deleting template: {}", templateId);
        NotificationTemplate template = templateRepository.findById(templateId);
        templateRepository.deleteAllTranslations(template.getId());
        templateRepository.deleteById(template.getId());
        log.info("Template deleted successfully: {}", templateId);
    }

    @Override
    public NotificationTemplate getTemplate(String templateId) {
        return templateRepository.findById(templateId);
    }

    @Override
    public TemplatesResult searchTemplates(SearchTemplatesQuery query) {
        // Get all templates, then filter
        var allTemplates = templateRepository.findByChannelAndIsActive(query.getChannel(), query.getIsActive());

        var filtered = allTemplates.stream()
            .filter(template ->
                query.getIsActive() == null ||
                template.getIsActive().equals(query.getIsActive()))
            .filter(template ->
                query.getName() == null ||
                template.getName().toLowerCase().contains(query.getName().toLowerCase()))
            .toList();

        int totalElements = filtered.size();
        int totalPages = (int) Math.ceil((double) totalElements / query.getSize());
        var paged = filtered.stream()
            .skip((long) query.getPage() * query.getSize())
            .limit(query.getSize())
            .toList();

        return TemplatesResult.builder()
            .templates(paged)
            .page(query.getPage())
            .size(query.getSize())
            .totalElements(totalElements)
            .totalPages(totalPages)
            .build();
    }

    @Override
    public NotificationTemplate addTranslation(AddTranslationData addTranslationData) {
        log.info("Adding translation for template: {}, locale: {}",
            addTranslationData.getTemplateId(), addTranslationData.getLocale());

        var template = templateRepository.findById(addTranslationData.getTemplateId());
        templateRepository.findTranslation(template.getId(), addTranslationData.getLocale());
        TemplateTranslation translation = templateSupport.buildTemplateTranslation(addTranslationData);
        templateRepository.saveTranslation(translation);
        return getTemplate(template.getId());
    }

    @Override
    public NotificationTemplate updateTranslation(UpdateTranslationData updateTranslationData) {
        log.info("Updating translation for template: {}, locale: {}",
            updateTranslationData.getTemplateId(), updateTranslationData.getLocale());

        var translation = templateRepository.findTranslation(updateTranslationData.getTemplateId(), updateTranslationData.getLocale());
        templateSupport.updateTemplateTranslation(translation, updateTranslationData);
        templateRepository.saveTranslation(translation);
        return getTemplate(translation.getId());
    }

    @Override
    public TemplateRenderResult renderTemplate(String templateId, Map<String, Object> variables, String locale) {
        log.info("Rendering template: {}, locale: {}", templateId, locale);

        templateRenderer.validateVariables(templateId, variables);

        var rendered = templateRenderer.renderTemplate(templateId, variables, locale);

        log.info("Template rendered successfully: {}", templateId);
        return TemplateRenderResult.builder()
            .templateId(templateId)
            .locale(locale)
            .renderedContent(rendered)
            .timestamp(LocalDateTime.now())
            .build();
    }


}
