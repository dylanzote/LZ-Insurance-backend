package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {
    private String id;
    private String name;
    private String description;
    private NotificationChannel channel;
    private String templatePath; // Filesystem path: e.g., "email/welcome-customer"
    private String subjectTemplate;
    private String bodyTemplate; // Plain text fallback
    private Map<String, TemplateVariable> variablesSchema;
    private String defaultLocale;
    private Boolean isActive;
    private Integer version;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
