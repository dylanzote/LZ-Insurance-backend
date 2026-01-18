package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.NotificationMetrics;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class NotificationMetricsResponse {
    private Long totalNotifications;
    private Long successfulNotifications;
    private Long failedNotifications;
    private Map<String, Long> notificationsByChannel;
    private Map<String, Long> notificationsByStatus;
    private Map<String, Long> notificationsByProvider;
    private Double averageDeliveryTimeMs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime toDate;

    public static NotificationMetricsResponse fromDomain(NotificationMetrics metrics) {
        var response = new NotificationMetricsResponse();
        BeanUtils.copyProperties(metrics, response);
        response.setNotificationsByChannel(convertEnumMap(metrics.getNotificationsByChannel()));
        response.setNotificationsByStatus(convertEnumMap(metrics.getNotificationsByStatus()));
        return response;
    }

    private static Map<String, Long> convertEnumMap(Map<? extends Enum<?>, Long> enumMap) {
        if (enumMap == null) return Map.of();
        return enumMap.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().name(),
                Map.Entry::getValue
            ));
    }
}
