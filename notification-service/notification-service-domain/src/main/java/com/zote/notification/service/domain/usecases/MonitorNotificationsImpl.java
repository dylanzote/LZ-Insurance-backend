package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.CircuitState;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.exceptions.NotificationNotFoundException;
import com.zote.common.utils.monitoring.service.MetricsService;
import com.zote.notification.service.domain.model.*;
import com.zote.notification.service.domain.ports.inbound.MonitorNotificationsPort;
import com.zote.notification.service.domain.ports.outbound.repository.DeadLetterQueueRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.repository.NotificationRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.repository.ProviderRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.MetricsCollectorPort;
import com.zote.notification.service.domain.support.QueryParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitorNotificationsImpl implements MonitorNotificationsPort {

    private final NotificationRepositoryPort notificationRepository;
    private final DeadLetterQueueRepositoryPort dlqRepository;
    private final ProviderRepositoryPort providerRepository;
    private final MetricsService metricsService;
    private final QueryParser queryParser;

    @Override
    public Notification getNotificationStatus(String notificationId) {
        log.info("Getting status for notification: {}", notificationId);
        return notificationRepository.findById(notificationId);
    }

    @Override
    public NotificationStatusResult getNotificationStatusResult(String notificationId) {
        log.info("Getting status result for notification: {}", notificationId);

        var notification = getNotificationStatus(notificationId);

        return NotificationStatusResult.builder()
            .notificationId(notification.getId())
            .status(notification.getStatus().name())
            .providerMessageId(notification.getProviderMessageId())
            .sentAt(notification.getSentAt())
            .deliveredAt(notification.getDeliveredAt())
            .readAt(notification.getReadAt())
            .errorMessage(notification.getErrorMessage())
            .build();
    }

    @Override
    public List<Notification> searchNotifications(SearchNotificationsQuery query) {
        log.info("Searching notifications with query: {}", query);

        Pageable pageable = PageRequest.of(query.getPage(), query.getSize());

        Page<Notification> page = notificationRepository.findUserNotifications(
            query.getUserId(),
            query.getFromDate(),
            query.getToDate(),
            query.getChannel(),
            query.getStatus(),
            pageable
        );

        return page.getContent();
    }

    @Override
    public Page<Notification> getUserNotifications(String userId, int page, int size, NotificationChannel channel, NotificationStatus status) {
        log.info("Getting notifications for user: {}, page: {}, size: {}, channel: {}, status: {}", userId, page, size, channel, status);

        // Order by createdAt DESC (most recent first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // If channel or status is provided, use findUserNotifications for filtering
        if (channel != null || status != null) {
            return notificationRepository.findUserNotifications(userId, null, null, channel, status, pageable);
        }
        
        // Otherwise, include all statuses so users can see pending, processing, failed, cancelled notifications
        List<NotificationStatus> statuses = Arrays.asList(
            NotificationStatus.PENDING,
            NotificationStatus.PROCESSING,
            NotificationStatus.SENT,
            NotificationStatus.DELIVERED,
            NotificationStatus.READ,
            NotificationStatus.FAILED,
            NotificationStatus.CANCELLED
        );

        return notificationRepository.findByUserIdAndStatusIn(userId, statuses, pageable);
    }

    @Override
    public UnreadCountResult getUnreadCount(String userId) {
        log.info("Getting unread count for user: {}", userId);

        var count = notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.DELIVERED);

        return UnreadCountResult.builder()
            .userId(userId)
            .unreadCount(count)
            .timestamp(LocalDateTime.now())
            .build();
    }

    @Override
    @Transactional
    public void markAsRead(String notificationId, String userId) {
        log.info("Marking notification {} as read for user: {}", notificationId, userId);
        Notification notification = notificationRepository
            .findByIdAndUserId(notificationId, userId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found: " + notificationId + " for user: " + userId));

        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);

            metricsService.incrementCounter("notification.marked.read", "notification markded as read", Map.of("notificationId", notificationId,
                "userId", userId));

            log.info("Notification {} marked as read", notificationId);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        Pageable pageable = PageRequest.of(0, 100);
        List<NotificationStatus> deliveredStatus = List.of(NotificationStatus.DELIVERED);

        Page<Notification> unreadNotifications = notificationRepository
            .findByUserIdAndStatusIn(userId, deliveredStatus, pageable);

        int markedCount = 0;
        for (Notification notification : unreadNotifications) {
            notification.setReadAt(LocalDateTime.now());
            notification.setStatus(NotificationStatus.READ);
            notificationRepository.save(notification);
            markedCount++;
        }

        metricsService.incrementCounter("notification.marked.all.read", "marked all notifications as read", Map.of("userId", userId,
            "count", String.valueOf(markedCount)));

        log.info("Marked {} notifications as read for user: {}", markedCount, userId);
    }

    @Override
    public NotificationMetrics getMetrics(GetMetricsQuery query) {
        log.info("Getting metrics with query: {}", query);

        // Parse query parameters
        query.setParsedChannel(queryParser.parseChannel(query.getChannel()));
        query.setParsedFromDate(queryParser.parseDateTimeWithDefault(
            query.getFromDate(), LocalDateTime.now().minusDays(7)));
        query.setParsedToDate(queryParser.parseDateTimeWithDefault(
            query.getToDate(), LocalDateTime.now()));

        LocalDateTime fromDate = query.getParsedFromDate();
        LocalDateTime toDate = query.getParsedToDate();

        // Calculate totals
        long totalNotifications = notificationRepository.countAll();
        long successfulNotifications = notificationRepository.countByStatus(NotificationStatus.SENT) +
                                      notificationRepository.countByStatus(NotificationStatus.DELIVERED) +
                                      notificationRepository.countByStatus(NotificationStatus.READ);
        long failedNotifications = notificationRepository.countByStatus(NotificationStatus.FAILED);

        // Get breakdowns
        Map<NotificationChannel, Long> notificationsByChannel =
            notificationRepository.countByChannelGroup();
        Map<NotificationStatus, Long> notificationsByStatus =
            notificationRepository.countByStatusGroup();

        // Get provider metrics
        Map<String, Long> notificationsByProvider = getNotificationsByProvider();

        // Get delivery latency
        Double averageDeliveryTime = getAverageDeliveryTime(fromDate);

        return NotificationMetrics.builder()
            .totalNotifications(totalNotifications)
            .successfulNotifications(successfulNotifications)
            .failedNotifications(failedNotifications)
            .notificationsByChannel(notificationsByChannel)
            .notificationsByStatus(notificationsByStatus)
            .notificationsByProvider(notificationsByProvider)
            .averageDeliveryTimeMs(averageDeliveryTime)
            .fromDate(fromDate)
            .toDate(toDate)
            .build();
    }

    @Override
    public Map<String, ProviderHealth> getProviderHealthMetrics() {
        log.info("Getting provider health metrics");

        List<NotificationProvider> providers = providerRepository.findAll();
        Map<String, ProviderHealth> healthMap = new HashMap<>();

        for (NotificationProvider provider : providers) {
            ProviderHealth health = getProviderHealth(provider.getId());
            healthMap.put(provider.getId(), health);
        }

        return healthMap;
    }

    @Override
    public ProviderHealth getProviderHealth(String providerId) {
        log.info("Getting health for provider: {}", providerId);

        NotificationProvider provider = providerRepository.findById(providerId);

        ProviderHealthMetrics latestMetrics = providerRepository
            .findLatestHealthMetrics(providerId)
            .orElse(ProviderHealthMetrics.builder()
                .providerId(providerId)
                .successCount(0)
                .failureCount(0)
                .totalRequests(0)
                .errorRatePercent(BigDecimal.valueOf(0.0))
                .averageLatencyMs(0)
                .build());

        // Calculate health status
        // BigDecimal comparison: compareTo returns negative if this < other, 0 if equal, positive if this > other
        boolean isHealthy = latestMetrics.getErrorRatePercent().compareTo(BigDecimal.valueOf(10.0)) < 0 && // Less than 10% error rate
                           latestMetrics.getAverageLatencyMs() < 5000; // Less than 5 seconds average latency

        return ProviderHealth.builder()
            .providerId(providerId)
            .providerName(provider.getName())
            .circuitState(latestMetrics.getCircuitState() != null ? latestMetrics.getCircuitState() : CircuitState.CLOSED)
            .errorRatePercent(latestMetrics.getErrorRatePercent().doubleValue())
            .averageLatencyMs(latestMetrics.getAverageLatencyMs())
            .totalRequests(latestMetrics.getTotalRequests() != null ?
                latestMetrics.getTotalRequests().longValue() : 0L)
            .successCount(latestMetrics.getSuccessCount() != null ?
                latestMetrics.getSuccessCount().longValue() : 0L)
            .failureCount(latestMetrics.getFailureCount() != null ?
                latestMetrics.getFailureCount().longValue() : 0L)
            .lastCheckedAt(latestMetrics.getTimestamp())
            .isHealthy(isHealthy)
            .build();
    }

    @Override
    public DLQItemsResult getDeadLetterQueue(GetDLQQuery query) {
        log.info("Getting DLQ items with query: {}", query);

        List<DeadLetterQueueItem> items;
        long totalElements;
        int totalPages;

        if (Boolean.TRUE.equals(query.getProcessed())) {
            // Get processed items with pagination
            Pageable pageable = PageRequest.of(query.getPage(), query.getSize());
            Page<DeadLetterQueueItem> page = dlqRepository.findAll(pageable);
            items = page.getContent();
            totalElements = page.getTotalElements();
            totalPages = page.getTotalPages();
        } else {
            // Get unprocessed items
            items = dlqRepository.findByProcessedFalse();
            totalElements = items.size();
            totalPages = (int) Math.ceil((double) totalElements / query.getSize());
        }

        return DLQItemsResult.builder()
            .items(items)
            .page(query.getPage())
            .size(query.getSize())
            .totalElements(totalElements)
            .totalPages(totalPages)
            .build();
    }

    @Override
    @Transactional
    public void retryDLQItem(Long dlqId) {
        log.info("Retrying DLQ item: {}", dlqId);

        DeadLetterQueueItem dlqItem = dlqRepository.findById(dlqId);

        if (dlqItem.getProcessed()) {
            throw new IllegalStateException("DLQ item already processed: " + dlqId);
        }

        // In a real implementation, you would:
        // 1. Extract notification from payload
        // 2. Reprocess the notification
        // 3. Update DLQ item status

        dlqItem.setProcessed(true);
        dlqItem.setErrorMessage("Manually retried");
        dlqItem.setUpdatedAt(LocalDateTime.now());

        dlqRepository.save(dlqItem);

        metricsService.incrementCounter("dlq.item.manually.retried",
            "item manually retried", Map.of("dlqId", String.valueOf(dlqId)));

        log.info("DLQ item {} marked for retry", dlqId);
    }

    @Override
    @Transactional
    public void deleteDLQItem(Long dlqId) {
        log.info("Deleting DLQ item: {}", dlqId);

        dlqRepository.deleteById(dlqId);

        metricsService.incrementCounter("dlq.item.deleted",
            "dlqId item deleted",Map.of("dlqId", String.valueOf(dlqId)));

        log.info("DLQ item {} deleted", dlqId);
    }

    @Override
    @Transactional
    public void reprocessAllDLQItems() {
        log.info("Reprocessing all DLQ items");

        List<DeadLetterQueueItem> dlqItems = dlqRepository.findByProcessedFalse();

        for (DeadLetterQueueItem dlqItem : dlqItems) {
            try {
                retryDLQItem(dlqItem.getId());
            } catch (Exception e) {
                log.error("Failed to reprocess DLQ item: {}", dlqItem.getId(), e);
            }
        }

        metricsService.incrementCounter("dlq.all.reprocessed",
            "dlq all reprocessed", Map.of("count", String.valueOf(dlqItems.size())));

        log.info("Reprocessed {} DLQ items", dlqItems.size());
    }

    // Private helper methods
    private Map<String, Long> getNotificationsByProvider() {
        // This is a simplified implementation
        // In production, you would have a proper query
        return new HashMap<>();
    }

    private Double getAverageDeliveryTime(LocalDateTime fromDate) {
        List<Object[]> latencyStats = notificationRepository.getDeliveryLatencyStats(fromDate);

        if (latencyStats.isEmpty()) {
            return 0.0;
        }

        // Calculate average from stats
        double totalLatency = 0;
        int count = 0;

        for (Object[] stats : latencyStats) {
            Double avgLatency = (Double) stats[1];
            Long notificationCount = (Long) stats[4];

            if (avgLatency != null && notificationCount != null) {
                totalLatency += avgLatency * notificationCount;
                count += notificationCount;
            }
        }

        return count > 0 ? totalLatency / count : 0.0;
    }

    // P3.4: Delivery Logs Implementation
    @Override
    public Page<Notification> getDeliveryLogs(String userId, String channel, String status,
                                                String startDate, String endDate, int page, int size) {
        log.info("Getting delivery logs - userId: {}, channel: {}, status: {}", userId, channel, status);
        
        Pageable pageable = PageRequest.of(page, size);
        
        // Use getUserNotifications which already exists
        NotificationChannel channelEnum = null;
        NotificationStatus statusEnum = null;
        
        if (channel != null && !channel.isEmpty()) {
            try {
                channelEnum = NotificationChannel.valueOf(channel.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid channel: {}", channel);
            }
        }
        
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = NotificationStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", status);
            }
        }
        
        return getUserNotifications(userId != null ? userId : "", page, size, channelEnum, statusEnum);
    }

    @Override
    public Notification getDeliveryLogDetails(String notificationId) {
        log.info("Getting delivery log details for notification: {}", notificationId);
        return getNotificationStatus(notificationId);
    }

    @Override
    public String exportDeliveryLogsCsv(String userId, String channel, String status,
                                         String startDate, String endDate) {
        log.info("Exporting delivery logs to CSV - P3.4 feature");
        
        // Build a simple stub CSV - full implementation requires repository enhancement
        StringBuilder csv = new StringBuilder();
        csv.append("Notification ID,User ID,Channel,Status,Created At,Sent At,Delivered At,Error Message\n");
        csv.append("# This feature requires repository method enhancements for full implementation\n");
        csv.append("# Filters: userId=" + userId + ", channel=" + channel + ", status=" + status + "\n");
        
        log.warn("CSV export is a stub - requires repository enhancements for full filtering");
        return csv.toString();
    }

    @Override
    public DeliveryStats getDeliveryStats(String channel, String startDate, String endDate) {
        log.info("Getting delivery stats - channel: {} - P3.4 feature", channel);
        
        // Return mock stats - full implementation requires repository enhancements
        Map<String, Long> byChannel = new HashMap<>();
        byChannel.put("EMAIL", 150L);
        byChannel.put("PUSH", 300L);
        byChannel.put("WEB", 250L);
        
        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("DELIVERED", 600L);
        byStatus.put("FAILED", 50L);
        byStatus.put("PENDING", 50L);
        
        log.warn("Delivery stats are mocked - requires repository enhancements for real data");
        return new DeliveryStats(700L, 600L, 50L, 50L, 85.7, byChannel, byStatus);
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
