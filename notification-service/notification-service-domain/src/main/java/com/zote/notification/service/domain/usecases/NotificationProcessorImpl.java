package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.NotificationStatus;
import com.zote.common.utils.monitoring.service.MetricsService;
import com.zote.kafka.adapter.event.NotificationFailedEvent;
import com.zote.kafka.adapter.event.NotificationSentEvent;
import com.zote.notification.service.domain.model.DeadLetterQueueItem;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.ports.outbound.repository.DeadLetterQueueRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.repository.NotificationRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProcessorImpl implements NotificationProcessorPort {

    private final NotificationRepositoryPort notificationRepository;
    private final DeadLetterQueueRepositoryPort dlqRepository;
    private final NotificationSenderPort notificationSender;
    private final WebSocketServicePort webSocketService;
    private final EventPublisherPort eventPublisher;
    private final MetricsService metricsService;



    @Override
    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    @Transactional
    public void reprocessDeadLetterQueue() {
        log.info("Starting DLQ reprocessing");

        List<DeadLetterQueueItem> dlqItems = dlqRepository
            .findByProcessedFalseAndScheduledRetryAtBefore(LocalDateTime.now());

        log.info("Found {} DLQ items to reprocess", dlqItems.size());

        for (DeadLetterQueueItem dlqItem : dlqItems) {
            try {
                log.info("Reprocessing DLQ item {} for notification {}",
                    dlqItem.getId(), dlqItem.getNotificationId());

                // Parse notification from payload
                Map<String, Object> payload = dlqItem.getPayload();
                @SuppressWarnings("unchecked")
                Map<String, Object> notificationData = (Map<String, Object>) payload.get("notification");

                // Find the original notification
                Notification notification;
                try {
                    notification = notificationRepository.findById(dlqItem.getNotificationId());
                } catch (Exception e) {
                    // If notification was deleted, create a simplified version from payload
                    log.warn("Original notification not found, creating from payload: {}", dlqItem.getNotificationId());
                    notification = Notification.builder()
                        .id(dlqItem.getNotificationId())
                        .userId((String) notificationData.get("userId"))
                        .channel(NotificationChannel.valueOf((String) notificationData.get("channel")))
                        .title((String) notificationData.get("title"))
                        .message((String) notificationData.get("message"))
                        .status(NotificationStatus.PENDING)
                        .retryCount(0)
                        .maxRetries(3)
                        .createdAt(LocalDateTime.now())
                        .build();
                }

                // Reset for reprocessing
                notification.setStatus(NotificationStatus.PENDING);
                notification.setRetryCount(0);
                notification.setErrorMessage(null);
                notification.setErrorStackTrace(null);

                // Save and reprocess
                notification = notificationRepository.save(notification);
                processNotification(notification);

                // Mark DLQ item as processed
                dlqItem.setProcessed(true);
                dlqItem.setErrorMessage("Reprocessed successfully");
                dlqRepository.save(dlqItem);

                log.info("Successfully reprocessed DLQ item {} for notification {}",
                    dlqItem.getId(), dlqItem.getNotificationId());

                metricsService.incrementCounter("dlq.item.reprocessed.success",
                    "dlqId", Map.of("dlqId",String.valueOf(dlqItem.getId())));

            } catch (Exception e) {
                log.error("Failed to reprocess DLQ item {}", dlqItem.getId(), e);

                // Update DLQ item with failure
                dlqItem.setRetryCount(dlqItem.getRetryCount() + 1);
                dlqItem.setErrorMessage("Reprocessing failed: " + e.getMessage());

                if (dlqItem.getRetryCount() >= 5) { // Max DLQ retries
                    dlqItem.setProcessed(true);
                    dlqItem.setErrorMessage("Max DLQ retries exceeded");
                    log.error("DLQ item {} permanently failed after {} retries",
                        dlqItem.getId(), dlqItem.getRetryCount());
                } else {
                    // Schedule for later retry
                    dlqItem.setScheduledRetryAt(LocalDateTime.now().plusHours(6));
                }

                dlqRepository.save(dlqItem);

                metricsService.incrementCounter("dlq.item.reprocessed.failed", "dlq item reprocessed failed", Map.of("dlqId", String.valueOf(dlqItem.getId()),
                    "error", e.getClass().getSimpleName()));
            }
        }
    }

    @Override
    @Transactional
    public Notification processNotification(Notification notification) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Processing notification {} for user {} via channel {}", 
                notification.getId(), notification.getUserId(), notification.getChannel());

            // Check if notification is scheduled for future
            if (notification.getScheduledAt() != null && 
                notification.getScheduledAt().isAfter(LocalDateTime.now())) {
                log.info("Notification {} is scheduled for {}, skipping processing",
                    notification.getId(), notification.getScheduledAt());
                notification.setStatus(NotificationStatus.PENDING);
                return notificationRepository.save(notification);
            }

            notification.setStatus(NotificationStatus.PROCESSING);
            notification = notificationRepository.save(notification);

            // Determine provider type based on channel
            ProviderType providerType = determineProviderType(notification);
            
            // Send notification via appropriate provider
            NotificationResult result = notificationSender.send(notification, providerType, notification.getChannel());

            long latencyMs = System.currentTimeMillis() - startTime;

            if (result.isSuccess()) {
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(LocalDateTime.now());
                notification.setProviderMessageId(result.getMessageId());
                notification.setRetryCount(0);
                notification.setErrorMessage(null);
                notification.setErrorStackTrace(null);

                // Send via WebSocket for real-time delivery (for PUSH channel)
                if (notification.getChannel() == NotificationChannel.PUSH) {
                    try {
                        webSocketService.sendNotificationToUser(notification.getUserId(), notification);
                    } catch (Exception e) {
                        log.warn("Failed to send WebSocket notification for user {}: {}", 
                            notification.getUserId(), e.getMessage());
                    }
                }

                // Publish success event
                publishNotificationSentEvent(notification, latencyMs);

                metricsService.incrementCounter("notification.sent.success", 
                    "notification sent successfully", 
                    Map.of("channel", notification.getChannel().name(),
                           "provider", providerType.name()));

            } else {
                // Failure - handle retry or DLQ
                handleNotificationFailure(notification, result, latencyMs, null);
            }

            notification = notificationRepository.save(notification);
            return notification;

        } catch (Exception e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            log.error("Error processing notification {}: {}", notification.getId(), e.getMessage(), e);
            
            handleNotificationFailure(notification, 
                NotificationResult.failed(e.getMessage()), 
                latencyMs, 
                e);
            
            return notificationRepository.save(notification);
        }
    }

    private ProviderType determineProviderType(Notification notification) {
        // If provider is already set, use it
        if (notification.getProvider() != null) {
            try {
                return ProviderType.valueOf(notification.getProvider().toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid provider type: {}, using default", notification.getProvider());
            }
        }

        // Default provider selection based on channel
        return switch (notification.getChannel()) {
            case EMAIL -> ProviderType.SMTP; // Use SMTP for Gmail
            case PUSH -> ProviderType.EXPO; // Use Expo for push (can fallback to FCM)
            case SMS -> ProviderType.TWILIO;
            case WEB_SOCKET, IN_APP -> ProviderType.GENERIC_WEBHOOK; // Both use WebSocket adapter
            default -> throw new IllegalArgumentException("Unsupported channel: " + notification.getChannel());
        };
    }

    private void handleNotificationFailure(Notification notification, NotificationResult result, 
                                         long latencyMs, Exception exception) {
        int currentRetryCount = notification.getRetryCount() != null ? notification.getRetryCount() : 0;
        int maxRetries = notification.getMaxRetries() != null ? notification.getMaxRetries() : 3;

        if (currentRetryCount < maxRetries) {
            // Retry
            notification.setStatus(NotificationStatus.PENDING);
            notification.setRetryCount(currentRetryCount + 1);
            notification.setErrorMessage(result.getError());
            if (exception != null) {
                notification.setErrorStackTrace(getStackTrace(exception));
            }
            
            log.info("Notification {} failed, will retry (attempt {}/{})", 
                notification.getId(), currentRetryCount + 1, maxRetries);
            
            metricsService.incrementCounter("notification.send.failed.retry", 
                "notification send failed, will retry", 
                Map.of("channel", notification.getChannel().name(),
                       "retryCount", String.valueOf(currentRetryCount + 1)));
        } else {
            // Max retries exceeded - move to DLQ
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(result.getError());
            if (exception != null) {
                notification.setErrorStackTrace(getStackTrace(exception));
            }
            
            log.error("Notification {} failed after {} retries, moving to DLQ", 
                notification.getId(), currentRetryCount);
            
            moveToDeadLetterQueue(notification, exception != null ? exception : new RuntimeException(result.getError()));
            
            publishNotificationFailedEvent(notification, latencyMs);
            
            metricsService.incrementCounter("notification.send.failed.dlq", 
                "notification send failed, moved to DLQ", 
                Map.of("channel", notification.getChannel().name()));
        }
    }

    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    @Override
    public void scheduleNotification(Notification notification) {
        if (notification.getScheduledAt() == null) {
            throw new IllegalArgumentException("Cannot schedule notification without scheduledAt time");
        }

        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        log.info("Scheduled notification {} for {}",
            notification.getId(), notification.getScheduledAt());

        metricsService.incrementCounter("notification.scheduled", "notification scheduled", Map.of("channel", notification.getChannel().name(),
            "scheduledAt", notification.getScheduledAt().toString()));
    }

    @Override
    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void processScheduledNotifications() {
        log.debug("Processing scheduled notifications");

        try {
            List<Notification> scheduledNotifications = notificationRepository
                .findByStatusAndScheduledAtBefore(NotificationStatus.PENDING, LocalDateTime.now());

            log.info("Found {} scheduled notifications to process", scheduledNotifications.size());

            for (Notification notification : scheduledNotifications) {
                try {
                    processNotification(notification);
                } catch (Exception e) {
                    log.error("Failed to process scheduled notification {}: {}", 
                        notification.getId(), e.getMessage(), e);
                }
            }

            metricsService.incrementCounter("notification.scheduled.processed", 
                "scheduled notifications processed", 
                Map.of("count", String.valueOf(scheduledNotifications.size())));

        } catch (Exception e) {
            log.error("Error processing scheduled notifications", e);
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void retryFailedNotifications() {
        log.debug("Retrying failed notifications");

        try {
            List<Notification> failedNotifications = notificationRepository
                .findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3);

            log.info("Found {} failed notifications to retry", failedNotifications.size());

            for (Notification notification : failedNotifications) {
                try {
                    // Reset status and retry
                    notification.setStatus(NotificationStatus.PENDING);
                    notification.setErrorMessage(null);
                    notification.setErrorStackTrace(null);
                    notification = notificationRepository.save(notification);
                    
                    processNotification(notification);
                } catch (Exception e) {
                    log.error("Failed to retry notification {}: {}", 
                        notification.getId(), e.getMessage(), e);
                }
            }

            metricsService.incrementCounter("notification.retry.attempted", 
                "failed notifications retried", 
                Map.of("count", String.valueOf(failedNotifications.size())));

        } catch (Exception e) {
            log.error("Error retrying failed notifications", e);
        }
    }

    @Override
    public void moveToDeadLetterQueue(Notification notification, Exception error) {
        try {
            log.info("Moving notification {} to Dead Letter Queue", notification.getId());

            // Create DLQ item
            Map<String, Object> payload = Map.of(
                "notification", Map.of(
                    "id", notification.getId(),
                    "userId", notification.getUserId() != null ? notification.getUserId() : "",
                    "channel", notification.getChannel().name(),
                    "title", notification.getTitle() != null ? notification.getTitle() : "",
                    "message", notification.getMessage() != null ? notification.getMessage() : "",
                    "metadata", notification.getMetadata() != null ? notification.getMetadata() : Map.of()
                )
            );

            DeadLetterQueueItem dlqItem = DeadLetterQueueItem.builder()
                .notificationId(notification.getId())
                .providerId(notification.getProvider())
                .errorType(error != null ? error.getClass().getName() : "Unknown")
                .errorMessage(notification.getErrorMessage() != null ? notification.getErrorMessage() : 
                    (error != null ? error.getMessage() : "Unknown error"))
                .errorStackTrace(notification.getErrorStackTrace() != null ? notification.getErrorStackTrace() : 
                    (error != null ? getStackTrace(error) : null))
                .payload(payload)
                .retryCount(0)
                .scheduledRetryAt(LocalDateTime.now().plusHours(6)) // Retry after 6 hours
                .processed(false)
                .createdAt(LocalDateTime.now())
                .build();

            dlqRepository.save(dlqItem);

            log.info("Notification {} moved to DLQ with ID {}", notification.getId(), dlqItem.getId());

            metricsService.incrementCounter("notification.moved.to.dlq", 
                "notification moved to dead letter queue", 
                Map.of("channel", notification.getChannel().name(),
                       "errorType", error != null ? error.getClass().getSimpleName() : "Unknown"));

        } catch (Exception e) {
            log.error("Failed to move notification {} to DLQ", notification.getId(), e);
        }
    }

    private void publishNotificationSentEvent(Notification notification, long latencyMs) {
        try {
            NotificationSentEvent event = NotificationSentEvent.builder()
                .eventId(generateEventId())
                .occurredAt(LocalDateTime.now())
                .correlationId(notification.getCorrelationId())
                .notificationId(notification.getId())
                .status(notification.getStatus())
                .providerId(notification.getProvider())
                .providerMessageId(notification.getProviderMessageId())
                .latencyMs(latencyMs)
                .build();

            eventPublisher.publish(event);
            log.debug("Published NotificationSentEvent for notification: {}", notification.getId());
        } catch (Exception e) {
            log.error("Failed to publish NotificationSentEvent for notification: {}", notification.getId(), e);
        }
    }

    private void publishNotificationFailedEvent(Notification notification, long latencyMs) {
        try {
            NotificationFailedEvent event = NotificationFailedEvent.builder()
                .eventId(generateEventId())
                .occurredAt(LocalDateTime.now())
                .correlationId(notification.getCorrelationId())
                .notificationId(notification.getId())
                .errorType(notification.getErrorMessage() != null ? "NotificationFailure" : "Unknown")
                .errorMessage(notification.getErrorMessage())
                .retryCount(notification.getRetryCount())
                .build();

            eventPublisher.publish(event);
            log.debug("Published NotificationFailedEvent for notification: {}", notification.getId());
        } catch (Exception e) {
            log.error("Failed to publish NotificationFailedEvent for notification: {}", notification.getId(), e);
        }
    }

    private String generateEventId() {
        return UUID.randomUUID().toString();
    }
}
