package com.zote.resourcebundle.adapter.domain.ports.outbound;

import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageTemplateRepositoryPort {

    MessageTemplate saveMessageTemplate(MessageTemplate messageTemplate);

    void deleteMessageTemplateById(String messageTemplateId);

    MessageTemplate findMessageTemplateById(String messageTemplateId);

    MessageTemplate findMessageTemplateByKeyAndLanguage(String name, Language language);

    Page<MessageTemplate> findTemplateByPage(Pageable pageable);

    List<MessageTemplate> getAllMessageTemplates();

    boolean messageTemplateById(String messageTemplateId);

    boolean existByNameAndLanguage(String name, Language language);
}
