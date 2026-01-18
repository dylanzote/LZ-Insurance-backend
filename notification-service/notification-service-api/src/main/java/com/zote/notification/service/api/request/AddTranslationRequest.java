package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.AddTranslationData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AddTranslationRequest {
    @NotBlank
    private String subject;

    @NotBlank
    private String body;

    private String htmlBody;

    public AddTranslationData toAddTranslationCommand(String templateId, String locale) {
        var command = new AddTranslationData();
        BeanUtils.copyProperties(this, command);
        command.setTemplateId(templateId);
        command.setLocale(locale);
        return command;
    }
}
