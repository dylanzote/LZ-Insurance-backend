package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
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
public class WebSocketNotificationMessage {
    private String id;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private NotificationPriority priority;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}
