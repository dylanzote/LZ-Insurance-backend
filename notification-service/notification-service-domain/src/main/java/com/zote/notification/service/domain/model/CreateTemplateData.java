package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTemplateData {
    private String name;
    private String description;
    private NotificationChannel channel;
    private String templatePath; // Filesystem path like "email/welcome-customer"
    private String subjectTemplate;
    private String bodyTemplate;
    private Map<String, TemplateVariable> variablesSchema;
    private String defaultLocale;
    private Boolean isActive;
}
