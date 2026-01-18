package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.NotificationTemplate;
import com.zote.notification.service.domain.model.TemplateVariable;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TemplateResponse {
    private String id;
    private String name;
    private String description;
    private String channel;
    private String subjectTemplate;
    private String bodyTemplate;
    private String htmlTemplate;
    private Map<String, TemplateVariable> variablesSchema;
    private String defaultLocale;
    private Boolean isActive;
    private Integer version;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<TemplateTranslationResponse> translations;

    public static TemplateResponse fromDomain(NotificationTemplate template) {
        var response = new TemplateResponse();
        BeanUtils.copyProperties(template, response);
        if (template.getChannel() != null) {
            response.setChannel(template.getChannel().name());
        }
        return response;
    }
}
