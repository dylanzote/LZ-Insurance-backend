package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.ports.outbound.repository.NotificationRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification save(Notification notification) {
        log.info("saving notification: {}", notification);
        return notificationRepository.save(NotificationEntity.toEntity(notification)).toDto();
    }


    @Override
    public Notification findById(String notificationId) {
        log.info("finding notification with id: {}", notificationId);
        return notificationRepository.findById(notificationId)
                .map(NotificationEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find notification with id " + notificationId));
    }

    @Override
    public Optional<Notification> findByIdAndUserId(String id, String userId) {
        log.info("finding notification with id: {} and userId: {}", id, userId);
        return notificationRepository.findById(id)
                .filter(entity -> entity.getUserId().equals(userId))
                .map(NotificationEntity::toDto);
    }

    @Override
    public Notification findByIdempotencyKey(String idempotencyKey) {
        log.info("finding notification with idempotencyKey: {}", idempotencyKey);
        return notificationRepository.findByIdempotencyKey(idempotencyKey)
                .map(NotificationEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find notification with idempotencyKey " + idempotencyKey));
    }

    @Override
    public List<Notification> findByStatusAndScheduledAtBefore(NotificationStatus status, LocalDateTime scheduledAt) {
        log.info("finding notifications with status: {} and scheduledAt before: {}", status, scheduledAt);
        return notificationRepository.findByStatusAndScheduledAtBefore(status, scheduledAt).stream()
                .map(NotificationEntity::toDto)
                .toList();
    }

    @Override
    public Page<Notification> findByUserIdAndStatusIn(String userId, List<NotificationStatus> statuses, Pageable pageable) {
        log.info("finding notifications for userId: {} with statuses: {}", userId, statuses);
        return notificationRepository.findByUserIdAndStatusIn(userId, statuses, pageable)
                .map(NotificationEntity::toDto);
    }

    @Override
    public long countByUserIdAndStatus(String userId, NotificationStatus status) {
        log.info("counting notifications for userId: {} with status: {}", userId, status);
        return notificationRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public Page<Notification> findUserNotifications(String userId, LocalDateTime fromDate, LocalDateTime toDate, NotificationChannel channel, NotificationStatus status, Pageable pageable) {
        log.info("finding user notifications for userId: {} fromDate: {} toDate: {} channel: {} status: {}", userId, fromDate, toDate, channel, status);
        return notificationRepository.findUserNotifications(userId, fromDate, toDate, channel, status, pageable)
                .map(NotificationEntity::toDto);
    }

    @Override
    public List<Object[]> getDailyMetrics(LocalDateTime fromDate, LocalDateTime toDate) {
        log.info("getting daily metrics fromDate: {} toDate: {}", fromDate, toDate);
        return notificationRepository.getDailyMetrics(fromDate, toDate);
    }

    @Override
    public List<Notification> findPendingScheduledNotifications() {
        log.info("finding pending scheduled notifications");
        return notificationRepository.findByStatusAndScheduledAtBefore(
                        NotificationStatus.PENDING,
                        LocalDateTime.now()
                ).stream()
                .map(NotificationEntity::toDto)
                .toList();
    }

    @Override
    public List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries) {
        log.info("finding notifications with status: {} and retryCount less than: {}", status, maxRetries);
        return notificationRepository.findByStatusAndRetryCountLessThan(status, maxRetries).stream()
                .map(NotificationEntity::toDto)
                .toList();
    }

    @Override
    public Map<NotificationStatus, Long> countByStatusGroup() {
        log.info("counting notifications by status group");
        List<Object[]> results = notificationRepository.countByStatusGroup();
        Map<NotificationStatus, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            NotificationStatus status = (NotificationStatus) result[0];
            Long count = (Long) result[1];
            counts.put(status, count);
        }

        return counts;
    }

    @Override
    public Map<NotificationChannel, Long> countByChannelGroup() {
        log.info("counting notifications by channel group");
        List<Object[]> results = notificationRepository.countByChannelGroup();
        Map<NotificationChannel, Long> counts = new HashMap<>();

        for (Object[] result : results) {
            NotificationChannel channel = (NotificationChannel) result[0];
            Long count = (Long) result[1];
            counts.put(channel, count);
        }

        return counts;
    }

    @Override
    public List<Object[]> getDeliveryLatencyStats(LocalDateTime fromDate) {
        log.info("getting delivery latency stats fromDate: {}", fromDate);
        return notificationRepository.getDeliveryLatencyStats(fromDate);
    }

    @Override
    public long countAll() {
        log.info("counting all notifications");
        return notificationRepository.count();
    }

    @Override
    public long countByStatus(NotificationStatus status) {
        log.info("counting notifications with status: {}", status);
        return notificationRepository.countByStatus(status);
    }
}
