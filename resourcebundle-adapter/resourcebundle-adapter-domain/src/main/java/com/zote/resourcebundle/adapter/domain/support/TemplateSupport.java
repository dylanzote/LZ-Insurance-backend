package com.zote.resourcebundle.adapter.domain.support;

import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TemplateSupport {

    public void updateTemplate (MessageTemplate messageTemplate, MessageTemplate messageTemplateData) {
        log.info("Updating template");
        messageTemplate.setBody(messageTemplateData.getBody());
        messageTemplate.setSubject(messageTemplateData.getSubject());
    }
}
