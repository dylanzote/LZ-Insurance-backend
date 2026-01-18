package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.FailedLoginEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.service.NotificationPreferenceService;
import com.zote.notification.service.domain.usecases.UserValidationService;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles FAILED_LOGIN events with MULTI-CHANNEL support.
 *
 * Sends security alert via:
 * - EMAIL (always)
 * - PUSH/WebSocket (based on user preferences)
 *
 * This is a CRITICAL security notification.
 *
 * Responsibilities:
 * - Alert user about failed login attempts
 * - Include attempt details (count, IP, timestamp)
 * - Provide link to change password
 * - Send via multiple channels for immediate awareness
 */
/**
 * Handles FAILED_LOGIN events with MULTI-CHANNEL support.
 *
 * Sends security alert via:
 * - EMAIL (always)
 * - PUSH/WebSocket (based on user preferences)
 *
 * This is a CRITICAL security notification.
 *
 * Responsibilities:
 * - Alert user about failed login attempts
 * - Include attempt details (count, IP, timestamp)
 * - Provide link to change password
 * - Send via multiple channels for immediate awareness
 */
@Slf4j
@Component
public class FailedLoginEventHandler extends MultiChannelEventHandler<FailedLoginEvent> {

    private final TemplateContextBuilder contextBuilder;

    public FailedLoginEventHandler(
            SendNotificationPort sendNotificationPort,
            UserValidationService userValidationService,
            NotificationPreferenceService notificationPreferenceService,
            TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort, userValidationService, notificationPreferenceService);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.FAILED_LOGIN;
    }

    @Override
    protected String extractUserId(FailedLoginEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(FailedLoginEvent event) {
        return "failed-login";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(FailedLoginEvent event) {
        LocalDateTime attemptedAt = event.getAttemptedAt() != null
                ? event.getAttemptedAt()
                : LocalDateTime.now();

        return contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withSecurityDetails(event.getFailedAttempts(), event.getIpAddress(), attemptedAt)
                .withChangePasswordUrl()
                .withSystemConstants()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(FailedLoginEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("failedAttempts", event.getFailedAttempts());
        if (event.getIpAddress() != null) {
            metadata.put("ipAddress", event.getIpAddress());
        }
        return metadata;
    }

    @Override
    protected String resolveLocale(FailedLoginEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(FailedLoginEvent event) {
        return NotificationType.SECURITY;
    }

    @Override
    protected NotificationPriority getPriority(FailedLoginEvent event) {
        return NotificationPriority.HIGH;
    }

    @Override
    protected boolean isCritical() {
        return true; // Security alert - send via multiple channels
    }
}
