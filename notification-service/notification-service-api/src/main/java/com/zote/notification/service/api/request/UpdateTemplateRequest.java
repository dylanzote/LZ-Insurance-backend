package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.TemplateVariable;
import com.zote.notification.service.domain.model.UpdateTemplateData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
public class UpdateTemplateRequest {
    private String name;

    private String description;

    private String subjectTemplate;

    private String bodyTemplate;

    private String htmlTemplate;

    private Map<String, TemplateVariable> variablesSchema;

    private Boolean isActive;

    public UpdateTemplateData toUpdateTemplateCommand(String templateId) {
        var command = new UpdateTemplateData();
        BeanUtils.copyProperties(this, command);
        command.setTemplateId(templateId);
        return command;
    }
}
