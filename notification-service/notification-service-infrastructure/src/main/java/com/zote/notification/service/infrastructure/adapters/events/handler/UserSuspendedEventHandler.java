package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.UserSuspendedEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.service.NotificationPreferenceService;
import com.zote.notification.service.domain.usecases.UserValidationService;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles USER_SUSPENDED events with MULTI-CHANNEL support.
 *
 * Sends notification via:
 * - EMAIL (always)
 * - PUSH/WebSocket (based on user preferences)
 *
 * This is a CRITICAL notification (admin action).
 *
 * Responsibilities:
 * - Notify user about account suspension
 * - Include suspension reason if provided
 * - Provide contact information for support
 * - Send via multiple channels for immediate awareness
 */
@Slf4j
@Component
public class UserSuspendedEventHandler extends MultiChannelEventHandler<UserSuspendedEvent> {

    private final TemplateContextBuilder contextBuilder;

    public UserSuspendedEventHandler(SendNotificationPort sendNotificationPort, UserValidationService userValidationService, NotificationPreferenceService notificationPreferenceService, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort, userValidationService, notificationPreferenceService);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_SUSPENDED;
    }

    @Override
    protected String extractUserId(UserSuspendedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(UserSuspendedEvent event) {
        return "user-suspended";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(UserSuspendedEvent event) {
        return contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withSuspensionReason(event.getSuspensionReason())
                .withSystemConstants()
                .withFooter()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(UserSuspendedEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("suspendedBy", event.getSuspendedBy() != null ? event.getSuspendedBy() : "SYSTEM");
        if (event.getSuspensionReason() != null) {
            metadata.put("reason", event.getSuspensionReason());
        }
        return metadata;
    }

    @Override
    protected String resolveLocale(UserSuspendedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(UserSuspendedEvent event) {
        return NotificationType.TRANSACTIONAL;
    }

    @Override
    protected NotificationPriority getPriority(UserSuspendedEvent event) {
        return NotificationPriority.HIGH;
    }

    @Override
    protected boolean isCritical() {
        return true; // Admin action - send via multiple channels
    }
}