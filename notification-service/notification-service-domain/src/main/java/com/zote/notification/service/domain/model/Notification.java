package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.enums.NotificationType;
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
public class Notification {
    private String id;
    private String userId;
    private String correlationId;
    private String idempotencyKey;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationPriority priority;
    private NotificationStatus status;
    private String provider;
    private String providerMessageId;
    private Map<String, Object> metadata;
    private Map<String, Object> templateVariables;
    private String locale;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private Integer retryCount;
    private Integer maxRetries;
    private String errorMessage;
    private String errorStackTrace;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
