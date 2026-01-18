package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.notification.service.domain.model.SendNotificationData;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.service.NotificationPreferenceService;
import com.zote.notification.service.domain.usecases.UserValidationService;
import com.zote.notification.service.infrastructure.adapters.events.BaseEventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

/**
 * Base handler for events that require multi-channel notifications.
 *
 * Design Pattern: Template Method + Strategy
 *
 * Used for CRITICAL notifications that should be sent via:
 * - EMAIL (always)
 * - PUSH (for mobile users)
 * - WEB_SOCKET (for web users)
 *
 * Examples: Security alerts, admin actions (suspend, activate)
 */
@Slf4j
public abstract class MultiChannelEventHandler<T extends DataEvent> extends BaseEventHandler<T> {

    protected final UserValidationService userValidationService;
    protected final NotificationPreferenceService notificationPreferenceService;

    public MultiChannelEventHandler(SendNotificationPort sendNotificationPort, UserValidationService userValidationService, NotificationPreferenceService notificationPreferenceService) {
        super(sendNotificationPort);
        this.userValidationService = userValidationService;
        this.notificationPreferenceService = notificationPreferenceService;
    }

    /**
     * Override handle to send multi-channel notifications
     */
    @Override
    public void handle(T event) {
        try {
            log.info("Processing multi-channel {} event for user: {}", getEventType(), extractUserId(event));

            String templateId = selectTemplate(event);
            Map<String, Object> templateVariables = buildTemplateContext(event);
            Map<String, Object> metadata = buildMetadata(event);
            String userId = extractUserId(event);
            String locale = resolveLocale(event);

            // Send to multiple channels
            sendMultiChannelNotification(
                userId,
                templateId,
                templateVariables,
                metadata,
                locale,
                getNotificationType(event),
                buildIdempotencyKey(event),
                isCritical()
            );

            log.info("Multi-channel {} notification sent to user: {} with template: {}",
                    getEventType(), userId, templateId);

        } catch (Exception e) {
            log.error("Failed to send multi-channel {} notification for user: {}",
                    getEventType(), extractUserId(event), e);
            throw e;
        }
    }

    /**
     * Determines if this is a critical notification (affects channel selection)
     */
    protected boolean isCritical() {
        return true; // Most multi-channel events are critical
    }

    /**
     * Send notifications to multiple channels based on user preferences
     */
    private void sendMultiChannelNotification(
            String userId,
            String templateId,
            Map<String, Object> templateVariables,
            Map<String, Object> metadata,
            String locale,
            NotificationType notificationType,
            String baseIdempotencyKey,
            boolean isCritical) {

        try {
            // Get user info and preferences
            var userInfo = userValidationService.validateAndGetUser(userId);
            var preference = notificationPreferenceService.getPreferenceForUser(userInfo);

            // Determine which channels to use
            Set<NotificationChannel> channels = notificationPreferenceService
                    .getChannelsForNotification(preference, isCritical);

            if (channels.isEmpty()) {
                log.warn("No channels enabled for user: {}, skipping notification", userId);
                return;
            }

            log.info("Sending multi-channel notification to user: {} via channels: {}",
                    userId, channels);

            // Send notification to each enabled channel
            int channelIndex = 0;
            for (NotificationChannel channel : channels) {
                try {
                    SendNotificationData notificationData = SendNotificationData.builder()
                            .userId(userId)
                            .templateId(templateId)
                            .templateVariables(templateVariables)
                            .channel(channel)
                            .type(notificationType)
                            .priority(isCritical ? NotificationPriority.HIGH : NotificationPriority.MEDIUM)
                            .locale(locale)
                            .metadata(metadata)
                            .idempotencyKey(baseIdempotencyKey + "-" + channel.name() + "-" + channelIndex++)
                            .build();

                    sendNotificationPort.sendNotification(notificationData);

                    log.info("Sent {} notification to user: {} for event: {}",
                            channel, userId, metadata.get("eventType"));

                } catch (Exception e) {
                    log.error("Failed to send {} notification to user: {}", channel, userId, e);
                    // Continue with other channels even if one fails
                }
            }

        } catch (Exception e) {
            log.error("Failed to send multi-channel notification to user: {}", userId, e);
            throw e;
        }
    }
}
