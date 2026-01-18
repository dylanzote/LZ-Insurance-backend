package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.*;
import com.zote.common.utils.exceptions.*;
import com.zote.common.utils.monitoring.service.MetricsService;
import com.zote.kafka.adapter.event.NotificationCreatedEvent;
import com.zote.notification.service.domain.model.*;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.ports.outbound.repository.NotificationRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.repository.UserPreferencesRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SendNotificationImpl implements SendNotificationPort {

    private final NotificationRepositoryPort notificationRepository;
    private final TemplateRendererPort templateRenderer;
    private final RateLimiterPort rateLimiter;
    private final EventPublisherPort eventPublisher;
    private final NotificationProcessorPort notificationProcessor;
    private final IdempotencyServicePort idempotencyService;
    private final UserPreferencesRepositoryPort preferencesRepository;
    private final MetricsService metricsService;

    @Override
    public Notification sendNotification(SendNotificationData sendNotificationData) {
        log.info("Processing send notification data for user: {}", sendNotificationData.getUserId());

        // 1. Idempotency check
        validateIdempotency(sendNotificationData);

        // 2. Rate limiting check
        validateRateLimits(sendNotificationData);

        // 3. Validate user preferences
        validateUserPreferences(sendNotificationData);

        // 4. Render templates if provided
        RenderResult renderResult = renderTemplates(sendNotificationData);

        // 5. Create notification
        Notification notification = createNotification(sendNotificationData, renderResult);

        // 6. Store idempotency key
        storeIdempotencyKey(sendNotificationData, notification);

        // 7. Save notification
        notification = notificationRepository.save(notification);

        // 8. Publish event
        publishNotificationCreatedEvent(notification);

        // 9. Process notification
        processNotification(notification);

        // 10. Collect metrics
        collectMetrics(sendNotificationData, notification);

        return notification;
    }

    @Override
    public BulkSendResult sendBulkNotifications(BulkNotificationData data) {
        log.info("Processing bulk notification for {} users", data.getUserIds().size());

        var notifications = data.getUserIds().stream()
            .map(userId -> {
                var singleData = SendNotificationData.builder()
                    .userId(userId)
                    .title(data.getTitle())
                    .message(data.getMessage())
                    .templateId(data.getTemplateId())
                    .templateVariables(data.getTemplateVariables())
                    .channel(data.getChannel())
                    .type(data.getType())
                    .priority(data.getPriority())
                    .locale(data.getLocale())
                    .metadata(data.getMetadata())
                    .scheduledAt(data.getScheduledAt())
                    .idempotencyKey(generateIdempotencyKey())
                    .build();

                return sendNotification(singleData);
            })
            .toList();

        return BulkSendResult.builder()
            .batchId(generateBatchId())
            .totalUsers(data.getUserIds().size())
            .processedCount(notifications.size())
            .timestamp(LocalDateTime.now())
            .notifications(notifications)
            .build();
    }

    @Override
    public Notification scheduleNotification(ScheduledNotificationData data) {
        log.info("Scheduling notification for user: {}", data.getUserId());

        var sendData = SendNotificationData.builder()
            .userId(data.getUserId())
            .title(data.getTitle())
            .message(data.getMessage())
            .templateId(data.getTemplateId())
            .templateVariables(data.getTemplateVariables())
            .channel(data.getChannel())
            .type(data.getType())
            .priority(NotificationPriority.MEDIUM) // Default priority for scheduled notifications
            .scheduledAt(data.getScheduledTime())
            .idempotencyKey(generateIdempotencyKey())
            .build();

        return sendNotification(sendData);
    }

    @Override
    public void cancelScheduledNotification(String notificationId) {
        log.info("Cancelling scheduled notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId);

        if (notification.getStatus() == NotificationStatus.PENDING &&
            notification.getScheduledAt() != null) {
            notification.setStatus(NotificationStatus.CANCELLED);
            notificationRepository.save(notification);

            metricsService.incrementCounter("notification.cancelled", "notification cancelled", Map.of(
                "notificationId", notificationId,
                "userId", notification.getUserId()
            ));

            log.info("Notification {} cancelled successfully", notificationId);
        } else {
            log.warn("Cannot cancel notification {} - status: {}, scheduled: {}",
                notificationId, notification.getStatus(), notification.getScheduledAt());
        }
    }

    private void validateIdempotency(SendNotificationData data) {
        if (idempotencyService.isDuplicate(data.getIdempotencyKey(), data.getUserId())) {
            throw new DuplicateNotificationException("Notification with idempotency key already processed: " + data.getIdempotencyKey());
        }
    }

    private void validateRateLimits(SendNotificationData data) {
        if (!rateLimiter.acquireToken(data.getUserId(), data.getChannel())) {
            throw new RateLimitExceededException("Rate limit exceeded for user: " + data.getUserId() + " on channel: " + data.getChannel());
        }
    }

    private void validateUserPreferences(SendNotificationData data) {
        if (Boolean.TRUE.equals(data.getBypassPreferences())) {
            return;
        }

        preferencesRepository.findByUserIdAndChannel(data.getUserId(), data.getChannel())
            .ifPresent(prefs -> {
                if (!Boolean.TRUE.equals(prefs.getEnabled())) {
                    throw new ChannelDisabledException("Channel " + data.getChannel() + " is disabled for user " + data.getUserId());
                }
                if (isWithinQuietHours(prefs)) {
                    throw new QuietHoursException("Cannot send notifications during quiet hours (" + prefs.getQuietHoursStart() + " - " + prefs.getQuietHoursEnd() + ")");
                }
            });
    }

    private RenderResult renderTemplates(SendNotificationData data) {
        String finalTitle = data.getTitle();
        String finalMessage = data.getMessage();
        String htmlContent = null;

        if (data.getTemplateId() != null && data.getTemplateVariables() != null) {
            try {
                String locale = determineLocale(data);
                
                log.info("Rendering template: {} with locale: {} for user: {}", 
                        data.getTemplateId(), locale, data.getUserId());

                // Validate template variables
                templateRenderer.validateVariables(data.getTemplateId(), data.getTemplateVariables());

                // Render subject (title)
                finalTitle = templateRenderer.renderSubject(
                    data.getTemplateId(),
                    data.getTemplateVariables(),
                    locale);

                // Render plain text body (fallback)
                finalMessage = templateRenderer.renderTemplate(
                    data.getTemplateId(),
                    data.getTemplateVariables(),
                    locale);

                // Render HTML content (primary)
                htmlContent = templateRenderer.renderHtml(
                    data.getTemplateId(),
                    data.getTemplateVariables(),
                    locale);
                    
                log.info("Template rendered successfully: title={}, hasHtml={}", 
                        finalTitle != null, htmlContent != null);

            } catch (Exception e) {
                log.error("Failed to render template: {} for user: {}. Using fallback message.", 
                        data.getTemplateId(), data.getUserId(), e);
                // Keep fallback values from command if template rendering fails
            }
        } else {
            log.debug("No template specified or no template variables provided. Using message from data.");
        }

        return new RenderResult(finalTitle, finalMessage, htmlContent);
    }

    private Notification createNotification(SendNotificationData data, RenderResult renderResult) {
        Notification notification = Notification.builder()
            .id(generateNotificationId())
            .userId(data.getUserId())
            .correlationId(data.getCorrelationId() != null ?
                data.getCorrelationId() : generateCorrelationId())
            .idempotencyKey(data.getIdempotencyKey())
            .title(renderResult.title())
            .message(renderResult.message())
            .type(data.getType() != null ? data.getType() : NotificationType.TRANSACTIONAL)
            .channel(data.getChannel())
            .priority(data.getPriority() != null ? data.getPriority() : NotificationPriority.MEDIUM)
            .status(NotificationStatus.PENDING)
            .locale(determineLocale(data))
            .scheduledAt(data.getScheduledAt())
            .metadata(data.getMetadata())
            .templateVariables(data.getTemplateVariables())
            .maxRetries(3)
            .retryCount(0)
            .build();
            
        // Store HTML content if available (for email rendering)
        if (renderResult.htmlContent() != null) {
            if (notification.getMetadata() == null) {
                notification.setMetadata(new java.util.HashMap<>());
            }
            notification.getMetadata().put("htmlContent", renderResult.htmlContent());
            log.debug("HTML content added to notification metadata for rendering");
        }
        
        return notification;
    }

    private void storeIdempotencyKey(SendNotificationData data, Notification notification) {
        idempotencyService.storeIdempotencyKey(data.getIdempotencyKey(), data.getUserId(), notification.getId());
    }

    private void publishNotificationCreatedEvent(Notification notification) {
        try {
            NotificationCreatedEvent event = NotificationCreatedEvent.builder()
                    .eventId(generateEventId())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(notification.getCorrelationId())
                    .notificationId(notification.getId())
                    .userId(notification.getUserId())
                    .channel(notification.getChannel())
                    .priority(notification.getPriority())
                    .build();
            
            eventPublisher.publish(event);
            log.info("Published NotificationCreatedEvent for notification: {}", notification.getId());
        } catch (Exception e) {
            log.error("Failed to publish NotificationCreatedEvent for notification: {}", notification.getId(), e);
            // Don't fail the notification creation if event publishing fails
        }
    }

    private void processNotification(Notification notification) {
        try {
            log.info("Processing notification {} for user: {}", notification.getId(), notification.getUserId());
            // Process the notification asynchronously (actual sending happens here)
            Notification processedNotification = notificationProcessor.processNotification(notification);
            
            if (processedNotification != null) {
                log.info("Notification {} processed successfully with status: {}", processedNotification.getId(), processedNotification.getStatus());
            } else {
                log.warn("Notification processor returned null for notification: {}", notification.getId());
            }
        } catch (Exception e) {
            log.error("Error processing notification: {}", notification.getId(), e);
            // The processor will handle retries and DLQ if needed
        }
    }

    private void collectMetrics(SendNotificationData sendNotificationData, Notification notification) {
        metricsService.incrementCounter("notification.created", "notification created", Map.of("channel", sendNotificationData.getChannel().name(),
            "type", sendNotificationData.getType().name(),
            "priority", sendNotificationData.getPriority().name()));

        if (notification.getScheduledAt() != null) {
            metricsService.incrementCounter("notification.scheduled",
                "notification scheduled", Map.of("channel", sendNotificationData.getChannel().name()));
        }
    }

    private String determineLocale(SendNotificationData sendNotificationData) {
        if (sendNotificationData.getLocale() != null) {
            return sendNotificationData.getLocale();
        }

        return preferencesRepository.findByUserId(sendNotificationData.getUserId())
            .map(UserNotificationPreferences::getLocale)
            .orElse("en");
    }

    private boolean isWithinQuietHours(UserNotificationPreferences prefs) {
        if (prefs.getQuietHoursStart() == null || prefs.getQuietHoursEnd() == null) {
            return false;
        }

        LocalTime now = LocalTime.now();
        return !now.isBefore(prefs.getQuietHoursStart()) &&
               !now.isAfter(prefs.getQuietHoursEnd());
    }

    private String generateNotificationId() {
        return "notif-" + UUID.randomUUID().toString();
    }

    private String generateCorrelationId() {
        return "corr-" + UUID.randomUUID().toString();
    }

    private String generateEventId() {
        return "evt-" + UUID.randomUUID().toString();
    }

    private String generateIdempotencyKey() {
        return "idemp-" + UUID.randomUUID().toString();
    }

    private String generateBatchId() {
        return "batch-" + UUID.randomUUID().toString();
    }

    // Record for render result
    private record RenderResult(String title, String message, String htmlContent) {}
}
