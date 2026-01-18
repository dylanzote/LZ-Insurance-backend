package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data model for scheduling notifications to be sent at a specific time
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledNotificationData {
    private String userId;
    private String title;
    private String message;
    private String templateId;
    private Map<String, Object> templateVariables;
    private NotificationChannel channel;
    private NotificationType type;
    private LocalDateTime scheduledTime;
    private String cronExpression;
}

