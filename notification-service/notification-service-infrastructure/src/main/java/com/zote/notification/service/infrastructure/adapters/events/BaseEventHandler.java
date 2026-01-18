package com.zote.notification.service.infrastructure.adapters.events;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.notification.service.domain.model.SendNotificationData;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.infrastructure.adapters.events.provider.UserEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseEventHandler<T extends DataEvent> implements UserEventHandler<T> {

    protected final SendNotificationPort sendNotificationPort;

    @Override
    public void handle(T event) {
        try {
            log.info("Processing {} event for user: {}", getEventType(), extractUserId(event));

            String templateId = selectTemplate(event);
            Map<String, Object> templateVariables = buildTemplateContext(event);
            Map<String, Object> metadata = buildMetadata(event);

            SendNotificationData notificationData = buildNotificationData(
                event,
                templateId,
                templateVariables,
                metadata
            );

            sendNotificationPort.sendNotification(notificationData);

            log.info("{} notification sent to user: {} with template: {}",
                    getEventType(), extractUserId(event), templateId);

        } catch (Exception e) {
            log.error("Failed to send {} notification for user: {}",
                    getEventType(), extractUserId(event), e);
            throw e;
        }
    }

    /**
     * Template Method: Subclasses must implement template selection logic
     */
    protected abstract String selectTemplate(T event);

    /**
     * Template Method: Subclasses must implement context building logic
     */
    protected abstract Map<String, Object> buildTemplateContext(T event);

    /**
     * Template Method: Subclasses must extract user ID from event
     */
    protected abstract String extractUserId(T event);

    /**
     * Template Method: Subclasses can override to customize notification type
     */
    protected NotificationType getNotificationType(T event) {
        return NotificationType.TRANSACTIONAL;
    }

    /**
     * Template Method: Subclasses can override to customize priority
     */
    protected NotificationPriority getPriority(T event) {
        return NotificationPriority.MEDIUM;
    }

    /**
     * Template Method: Subclasses can override to customize channel
     */
    protected NotificationChannel getChannel(T event) {
        return NotificationChannel.EMAIL;
    }

    /**
     * Template Method: Subclasses can override locale resolution
     */
    protected String resolveLocale(T event) {
        return "en"; // Default locale
    }

    /**
     * Default metadata builder - can be overridden
     */
    protected Map<String, Object> buildMetadata(T event) {
        return Map.of(
            "eventId", event.getEventId(),
            "correlationId", event.getCorrelationId(),
            "eventType", event.getEventType(),
            "occurredAt", event.getOccurredAt()
        );
    }

    /**
     * Consistent notification data building
     */
    private SendNotificationData buildNotificationData(
            T event,
            String templateId,
            Map<String, Object> templateVariables,
            Map<String, Object> metadata) {

        return SendNotificationData.builder()
                .userId(extractUserId(event))
                .templateId(templateId)
                .templateVariables(templateVariables)
                .channel(getChannel(event))
                .type(getNotificationType(event))
                .priority(getPriority(event))
                .locale(resolveLocale(event))
                .metadata(metadata)
                .idempotencyKey(buildIdempotencyKey(event))
                .build();
    }

    /**
     * Idempotency key generation - can be overridden
     */
    protected String buildIdempotencyKey(T event) {
        return String.format("%s:%s:%s",
            getEventType().name().toLowerCase(),
            extractUserId(event),
            event.getEventId()
        );
    }
}
