package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
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
public class NotificationMetrics {
    private Long totalNotifications;
    private Long successfulNotifications;
    private Long failedNotifications;
    private Map<NotificationChannel, Long> notificationsByChannel;
    private Map<NotificationStatus, Long> notificationsByStatus;
    private Double averageDeliveryTimeMs;
    private Map<String, Long> notificationsByProvider;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
