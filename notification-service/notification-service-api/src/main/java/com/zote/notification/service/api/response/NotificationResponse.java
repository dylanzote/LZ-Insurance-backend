package com.zote.notification.service.api.response;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.enums.NotificationType;
import com.zote.notification.service.domain.model.Notification;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {

    private String id;
    private String userId;
    private String title;
    private String message;
    private NotificationChannel channel;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationStatus status;
    private String providerMessageId;
    private String locale;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResponse toResponse(Notification notification) {
        NotificationResponse notificationResponse = new NotificationResponse();
        BeanUtils.copyProperties(notification, notificationResponse);
        return notificationResponse;
    }
}
