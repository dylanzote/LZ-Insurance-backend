package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationData {
    private String userId;

    private String correlationId;

    private String idempotencyKey;

    private String title;

    private String message;

    private String templateId;

    private Map<String, Object> templateVariables;

    private NotificationChannel channel;

    private NotificationType type;

    private NotificationPriority priority;

    private String locale; // Override user's default locale

    private Map<String, Object> metadata;

    private LocalDateTime scheduledAt; // null for immediate

    private Duration sendAfter; // Send after delay

    private Boolean bypassPreferences; // For critical notifications

    private List<String> cc;

    private List<String> bcc;

    private List<Attachment> attachments;
}
