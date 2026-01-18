package com.zote.resourcebundle.adapter.domain.ports.inbound;

import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface MessageTemplatePort {

    MessageTemplate createTemplate(MessageTemplate messageTemplate);

    MessageTemplate updateTemplate(MessageTemplate messageTemplate);

    void deleteTemplate(String templateId);

    MessageTemplate findTemplateById(String templateId);

    MessageTemplate findTemplateByLanguageAndName(Language language, String name);

    List<MessageTemplate> getAllTemplates();

    Page<MessageTemplate> getAllTemplatesByPage(int pageNo, int sizePerPage, String sortField, Sort.Direction sortDirection);

}
