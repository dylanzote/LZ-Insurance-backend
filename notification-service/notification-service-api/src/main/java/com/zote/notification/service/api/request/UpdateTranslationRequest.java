package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.UpdateTranslationData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UpdateTranslationRequest {
    private String subject;

    private String body;

    private String htmlBody;

    public UpdateTranslationData toUpdateTranslationCommand(String templateId, String locale) {
        var command = new UpdateTranslationData();
        BeanUtils.copyProperties(this, command);
        command.setTemplateId(templateId);
        command.setLocale(locale);
        return command;
    }
}
