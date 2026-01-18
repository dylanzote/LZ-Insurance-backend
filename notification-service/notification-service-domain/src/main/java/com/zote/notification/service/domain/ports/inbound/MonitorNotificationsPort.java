package com.zote.notification.service.domain.ports.inbound;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.notification.service.domain.model.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface MonitorNotificationsPort {
    Notification getNotificationStatus(String notificationId);
    NotificationStatusResult getNotificationStatusResult(String notificationId);
    List<Notification> searchNotifications(SearchNotificationsQuery query);
    Page<Notification> getUserNotifications(String userId, int page, int size, NotificationChannel channel, NotificationStatus status);
    UnreadCountResult getUnreadCount(String userId);
    void markAsRead(String notificationId, String userId);
    void markAllAsRead(String userId);

    // Metrics
    NotificationMetrics getMetrics(GetMetricsQuery query);
    Map<String, ProviderHealth> getProviderHealthMetrics();
    ProviderHealth getProviderHealth(String providerId);

    // DLQ Management
    DLQItemsResult getDeadLetterQueue(GetDLQQuery query);
    void retryDLQItem(Long dlqId);
    void deleteDLQItem(Long dlqId);
    void reprocessAllDLQItems();

    // P3.4: Delivery Logs
    Page<Notification> getDeliveryLogs(String userId, String channel, String status, 
                                        String startDate, String endDate, int page, int size);
    Notification getDeliveryLogDetails(String notificationId);
    String exportDeliveryLogsCsv(String userId, String channel, String status, 
                                  String startDate, String endDate);
    DeliveryStats getDeliveryStats(String channel, String startDate, String endDate);
    
    record DeliveryStats(
        long totalSent,
        long totalDelivered,
        long totalFailed,
        long totalPending,
        double deliveryRate,
        Map<String, Long> byChannel,
        Map<String, Long> byStatus
    ) {}
}
