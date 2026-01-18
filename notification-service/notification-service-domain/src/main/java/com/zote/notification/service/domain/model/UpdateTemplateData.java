package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTemplateData {
    private String templateId;
    private String name;
    private String description;
    private String templatePath; // Filesystem path like "email/welcome-customer"
    private String subjectTemplate;
    private String bodyTemplate;
    private Map<String, TemplateVariable> variablesSchema;
    private Boolean isActive;
}
