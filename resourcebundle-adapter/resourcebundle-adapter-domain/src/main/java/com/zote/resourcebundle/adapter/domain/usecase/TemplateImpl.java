package com.zote.resourcebundle.adapter.domain.usecase;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import com.zote.resourcebundle.adapter.domain.ports.inbound.MessageTemplatePort;
import com.zote.resourcebundle.adapter.domain.ports.outbound.MessageTemplateRepositoryPort;
import com.zote.resourcebundle.adapter.domain.support.TemplateSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateImpl implements MessageTemplatePort {

    private final MessageTemplateRepositoryPort templateRepository;

    private final TemplateSupport templateSupport;

    @Override
    public MessageTemplate createTemplate(MessageTemplate messageTemplate) {
        log.info("Creating template");
        verifyIfTemplateExist(messageTemplate.getName(), messageTemplate.getLanguage());
        messageTemplate.setId(UUID.randomUUID().toString());
        return templateRepository.saveMessageTemplate(messageTemplate);
    }

    @Override
    public MessageTemplate updateTemplate(MessageTemplate messageTemplate) {
        var template = templateRepository.findMessageTemplateById(messageTemplate.getId());
        templateSupport.updateTemplate(template, messageTemplate);
        return template;
    }

    @Override
    public void deleteTemplate(String templateId) {
        log.info("Deleting template");
        templateRepository.deleteMessageTemplateById(templateId);
    }

    @Override
    public MessageTemplate findTemplateById(String templateId) {
        return templateRepository.findMessageTemplateById(templateId);
    }

    @Override
    public MessageTemplate findTemplateByLanguageAndName(Language language, String name) {
        return templateRepository.findMessageTemplateByKeyAndLanguage(name, language);
    }

    @Override
    public List<MessageTemplate> getAllTemplates() {
        return templateRepository.getAllMessageTemplates();
    }

    @Override
    public Page<MessageTemplate> getAllTemplatesByPage(int page, int sizePerPage, String sortField, Sort.Direction sortDirection) {
        var pageNo = page < 0 ? 0 : page - 1;
        var pageable = PageRequest.of(pageNo, sizePerPage, sortDirection, sortField);
        return templateRepository.findTemplateByPage(pageable);
    }

    private void verifyIfTemplateExist(String name, Language language) {
        if (templateRepository.existByNameAndLanguage(name, language)) {
            throw new FunctionalError("template already exists");
        }
    }
}
