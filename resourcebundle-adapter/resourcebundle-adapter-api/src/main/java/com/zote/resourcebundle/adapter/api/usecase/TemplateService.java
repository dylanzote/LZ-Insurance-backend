package com.zote.resourcebundle.adapter.api.usecase;

import com.zote.common.utils.enums.SortField;
import com.zote.resourcebundle.adapter.api.controller.MessageTemplateApi;
import com.zote.resourcebundle.adapter.api.request.CreateTemplateRequest;
import com.zote.resourcebundle.adapter.api.request.GetTemplateRequest;
import com.zote.resourcebundle.adapter.api.request.UpdateTemplateRequest;
import com.zote.resourcebundle.adapter.api.response.TemplatePageResponse;
import com.zote.resourcebundle.adapter.api.response.TemplateResponse;
import com.zote.resourcebundle.adapter.domain.ports.inbound.MessageTemplatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TemplateService implements MessageTemplateApi {

    private final MessageTemplatePort messageTemplate;

    @Override
    public TemplateResponse createTemplate(CreateTemplateRequest request) {
        log.info("incoming request for creating template {}", request);
        return TemplateResponse.toResponse(messageTemplate.createTemplate(request.toMessageTemplate()));
    }

    @Override
    public List<TemplateResponse> getTemplates() {
        log.info("incoming request for getting all templates");
        return messageTemplate.getAllTemplates().stream().map(TemplateResponse::toResponse).toList();
    }

    @Override
    public TemplatePageResponse getAllTemplate(Integer pageNo, Integer sizePerPage, SortField sortField, Sort.Direction sortDirection) {
        log.info("incoming get all templates by page request with page {}, sizePerPage {}, SortField {}, SortOrder {}", pageNo, sizePerPage, sortField, sortDirection);
        return new TemplatePageResponse(messageTemplate.getAllTemplatesByPage(pageNo, sizePerPage, sortField.getFieldName(), sortDirection).map(TemplateResponse::toResponse));
    }

    @Override
    public TemplateResponse getTemplate(String id) {
        log.info("incoming get template by id request");
        return TemplateResponse.toResponse(messageTemplate.findTemplateById(id));
    }

    @Override
    public TemplateResponse getTemplate(GetTemplateRequest getTemplateRequest) {
        log.info("incoming get template by language and name request with language {}, templateName {}", getTemplateRequest.language(), getTemplateRequest.templateName());
        return TemplateResponse.toResponse(messageTemplate.findTemplateByLanguageAndName(getTemplateRequest.language(), getTemplateRequest.templateName()));
    }

    @Override
    public TemplateResponse updateTemplate(UpdateTemplateRequest request) {
        log.info("incoming update template request {}", request);
        return TemplateResponse.toResponse(messageTemplate.updateTemplate(request.toMessageTemplate()));
    }

    @Override
    public void deleteTemplate(String id) {
        log.info("incoming delete template by id request");
        messageTemplate.deleteTemplate(id);
    }
}
