package com.zote.notification.service.api.usecase;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.api.controller.TemplateApi;
import com.zote.notification.service.api.request.*;
import com.zote.common.utils.models.PageResponse;
import com.zote.notification.service.api.response.TemplateRenderResponse;
import com.zote.notification.service.api.response.TemplateResponse;
import com.zote.notification.service.domain.model.SearchTemplatesQuery;
import com.zote.notification.service.domain.ports.inbound.ManageTemplatesPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class TemplateService implements TemplateApi {
    private final ManageTemplatesPort manageTemplatesPort;

    @Override
    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        log.info("API: Creating template: {}", request.getName());
        return TemplateResponse.fromDomain(manageTemplatesPort.createTemplate(request.toCreateTemplateData()));
    }

    @Override
    public TemplateResponse updateTemplate(String templateId, UpdateTemplateRequest request) {
        log.info("API: Updating template: {}", templateId);
        var template = manageTemplatesPort.updateTemplate(request.toUpdateTemplateCommand(templateId));
        return TemplateResponse.fromDomain(template);
    }

    @Override
    public TemplateResponse getTemplate(String templateId) {
        log.info("API: Getting template: {}", templateId);
        var template = manageTemplatesPort.getTemplate(templateId);
        return TemplateResponse.fromDomain(template);
    }

    @Override
    public PageResponse searchTemplates(NotificationChannel channel, Boolean isActive, int page, int size) {
        log.info("API: Searching templates, channel: {}, isActive: {}", channel, isActive);
        var query = SearchTemplatesQuery.builder()
            .channel(channel)
            .isActive(isActive)
            .page(page)
            .size(size)
            .build();
        var result = manageTemplatesPort.searchTemplates(query);
        
        var content = result.getTemplates().stream()
            .map(TemplateResponse::fromDomain)
            .map(template -> (Object) template)
            .toList();
        
        var pageable = org.springframework.data.domain.PageRequest.of(page, size);
        var pageResult = new org.springframework.data.domain.PageImpl<>(
            content, pageable, result.getTotalElements());
        
        return new PageResponse(pageResult);
    }

    @Override
    public TemplateResponse addTranslation(String templateId, String locale, AddTranslationRequest request) {
        log.info("API: Adding translation for template: {}, locale: {}", templateId, locale);
        var template = manageTemplatesPort.addTranslation(request.toAddTranslationCommand(templateId, locale));
        return TemplateResponse.fromDomain(template);
    }

    @Override
    public TemplateResponse updateTranslation(String templateId, String locale, UpdateTranslationRequest request) {
        log.info("API: Updating translation for template: {}, locale: {}", templateId, locale);
        var template = manageTemplatesPort.updateTranslation(request.toUpdateTranslationCommand(templateId, locale));
        return TemplateResponse.fromDomain(template);
    }

    @Override
    public TemplateRenderResponse renderTemplate(String templateId, RenderTemplateRequest request) {
        log.info("API: Rendering template: {}", templateId);
        var result = manageTemplatesPort.renderTemplate(templateId, request.getVariables(), request.getLocale());
        return TemplateRenderResponse.toTemplateRenderResponse(result);
    }

    @Override
    public void deleteTemplate(String templateId) {
        log.info("API: Deleting template: {}", templateId);
        manageTemplatesPort.deleteTemplate(templateId);
    }
}
