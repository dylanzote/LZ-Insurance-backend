package com.zote.notification.service.domain.ports.inbound;

import com.zote.notification.service.domain.model.BulkNotificationData;
import com.zote.notification.service.domain.model.BulkSendResult;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.ScheduledNotificationData;
import com.zote.notification.service.domain.model.SendNotificationData;

/**
 * Inbound port for sending notifications
 * Supports single, bulk, and scheduled notifications
 */
public interface SendNotificationPort {

    Notification sendNotification(SendNotificationData data);

    BulkSendResult sendBulkNotifications(BulkNotificationData data);

    Notification scheduleNotification(ScheduledNotificationData data);

    void cancelScheduledNotification(String notificationId);
}
