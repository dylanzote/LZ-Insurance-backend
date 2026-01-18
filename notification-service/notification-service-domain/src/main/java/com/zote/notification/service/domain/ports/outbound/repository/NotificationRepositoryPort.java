package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.notification.service.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    Notification findById(String id);
    Optional<Notification> findByIdAndUserId(String id, String userId);
    Notification findByIdempotencyKey(String idempotencyKey);
    List<Notification> findByStatusAndScheduledAtBefore(NotificationStatus status, LocalDateTime scheduledAt);
    Page<Notification> findByUserIdAndStatusIn(String userId, List<NotificationStatus> statuses, Pageable pageable);
    long countByUserIdAndStatus(String userId, NotificationStatus status);
    Page<Notification> findUserNotifications(String userId, LocalDateTime fromDate, LocalDateTime toDate,
                                           NotificationChannel channel, NotificationStatus status, Pageable pageable);
    List<Object[]> getDailyMetrics(LocalDateTime fromDate, LocalDateTime toDate);
    List<Notification> findPendingScheduledNotifications();
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);

    Map<NotificationStatus, Long> countByStatusGroup();
    Map<NotificationChannel, Long> countByChannelGroup();
    List<Object[]> getDeliveryLatencyStats(LocalDateTime fromDate);
    long countAll();
    long countByStatus(NotificationStatus status);
}
