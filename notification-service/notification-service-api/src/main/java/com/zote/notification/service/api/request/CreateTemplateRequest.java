package com.zote.notification.service.api.request;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.CreateTemplateData;
import com.zote.notification.service.domain.model.TemplateVariable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private NotificationChannel channel;

    private String subjectTemplate;

    @NotBlank
    private String bodyTemplate;

    private String htmlTemplate;

    private Map<String, TemplateVariable> variablesSchema;

    private String defaultLocale;

    private Boolean isActive = true;

    public CreateTemplateData toCreateTemplateData() {
        var createTemplateData = new CreateTemplateData();
        BeanUtils.copyProperties(this, createTemplateData);
        return createTemplateData;
    }
}
