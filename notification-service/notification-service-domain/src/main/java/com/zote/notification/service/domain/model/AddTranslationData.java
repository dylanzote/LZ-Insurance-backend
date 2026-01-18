package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTranslationData {
    private String templateId;
    private String locale;
    private String subject;
    private String body;
    private String htmlBody;
}
