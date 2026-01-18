package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, String>, JpaSpecificationExecutor<NotificationEntity> {

    Optional<NotificationEntity> findByIdempotencyKey(String idempotencyKey);

    List<NotificationEntity> findByStatusAndScheduledAtBefore(
            NotificationStatus status, LocalDateTime scheduledAt);

    List<NotificationEntity> findByStatusAndRetryCountLessThan(
            NotificationStatus status, Integer maxRetries);

    Page<NotificationEntity> findByUserIdAndStatusIn(
            String userId, List<NotificationStatus> statuses, Pageable pageable);

    long countByUserIdAndStatus(String userId, NotificationStatus status);

    @Query("SELECT n FROM NotificationEntity n WHERE " +
            "n.userId = :userId AND " +
            "(:fromDate IS NULL OR n.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR n.createdAt <= :toDate) AND " +
            "(:channel IS NULL OR n.channel = :channel) AND " +
            "(:status IS NULL OR n.status = :status) " +
            "ORDER BY n.createdAt DESC")
    Page<NotificationEntity> findUserNotifications(
            @Param("userId") String userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            @Param("channel") NotificationChannel channel,
            @Param("status") NotificationStatus status,
            Pageable pageable);

    @Query(value = """
            SELECT 
                DATE(n.created_at) as date,
                n.channel,
                n.status,
                COUNT(*) as count
            FROM notifications n
            WHERE n.created_at >= :fromDate
            AND n.created_at <= :toDate
            GROUP BY DATE(n.created_at), n.channel, n.status
            ORDER BY date DESC
            """, nativeQuery = true)
    List<Object[]> getDailyMetrics(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query(value = """
            SELECT 
                n.channel,
                AVG(EXTRACT(EPOCH FROM (n.sent_at - n.created_at)) * 1000) as avg_latency,
                PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (n.sent_at - n.created_at)) * 1000) as p95_latency,
                PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY EXTRACT(EPOCH FROM (n.sent_at - n.created_at)) * 1000) as p99_latency,
                COUNT(*) as count
            FROM notifications n
            WHERE n.sent_at IS NOT NULL
            AND n.created_at >= :fromDate
            GROUP BY n.channel
            """, nativeQuery = true)
    List<Object[]> getDeliveryLatencyStats(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT n.status, COUNT(n) FROM NotificationEntity n GROUP BY n.status")
    List<Object[]> countByStatusGroup();

    @Query("SELECT n.channel, COUNT(n) FROM NotificationEntity n GROUP BY n.channel")
    List<Object[]> countByChannelGroup();

    long countByStatus(NotificationStatus status);
}
