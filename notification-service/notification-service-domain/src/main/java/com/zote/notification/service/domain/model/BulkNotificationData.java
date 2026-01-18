package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data model for sending bulk notifications to multiple users
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkNotificationData {
    private List<String> userIds;
    private String title;
    private String message;
    private String templateId;
    private Map<String, Object> templateVariables;
    private NotificationChannel channel;
    private NotificationType type;
    private NotificationPriority priority;
    private String locale;
    private Map<String, Object> metadata;
    private LocalDateTime scheduledAt;
}

