package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.common.utils.enums.NotificationStatus;
import com.zote.notification.service.domain.model.Alert;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationMetrics;
import com.zote.notification.service.domain.model.ProviderHealth;

public interface WebSocketServicePort {
    void sendNotificationToUser(String userId, Notification notification);
    void sendDeliveryStatus(String userId, String notificationId, NotificationStatus status);
    void sendProviderHealthUpdate(ProviderHealth health);
    void sendAlert(Alert alert);
    void sendMetricsUpdate(NotificationMetrics metrics);
}
