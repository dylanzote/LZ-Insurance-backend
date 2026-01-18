package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateTranslation {
    private String id;
    private String templateId;
    private String locale;
    private String subject; // Localized subject line
    private String body; // Optional plain text fallback
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
