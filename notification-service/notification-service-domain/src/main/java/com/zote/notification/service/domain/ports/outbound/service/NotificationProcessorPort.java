package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.notification.service.domain.model.Notification;

public interface NotificationProcessorPort {
    Notification processNotification(Notification notification);
    void scheduleNotification(Notification notification);
    void processScheduledNotifications();
    void retryFailedNotifications();
    void moveToDeadLetterQueue(Notification notification, Exception error);
    void reprocessDeadLetterQueue();
}
