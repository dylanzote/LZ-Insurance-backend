package com.zote.resourcebundle.adapter.api.request;

import com.zote.common.utils.enums.Language;
import com.zote.resourcebundle.adapter.domain.model.MessageTemplate;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

public record CreateTemplateRequest(
        @NotNull
        Language language,

        @NotNull
        String name,

        @NotNull
        String subject,

        @NotNull
        String body
) {
        public MessageTemplate toMessageTemplate() {
                MessageTemplate messageTemplate = new MessageTemplate();
                BeanUtils.copyProperties(this, messageTemplate);
                return messageTemplate;
        }

}
