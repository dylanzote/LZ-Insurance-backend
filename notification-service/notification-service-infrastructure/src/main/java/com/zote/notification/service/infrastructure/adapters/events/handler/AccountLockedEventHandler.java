package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.AccountLockedEvent;
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
 * Handles ACCOUNT_LOCKED events with MULTI-CHANNEL support.
 *
 * Sends critical security alert via:
 * - EMAIL (always)
 * - PUSH/WebSocket (based on user preferences)
 *
 * This is a CRITICAL security notification.
 */
@Slf4j
@Component
public class AccountLockedEventHandler extends MultiChannelEventHandler<AccountLockedEvent> {

    private final TemplateContextBuilder contextBuilder;

    public AccountLockedEventHandler(SendNotificationPort sendNotificationPort, UserValidationService userValidationService, NotificationPreferenceService notificationPreferenceService, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort, userValidationService, notificationPreferenceService);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.ACCOUNT_LOCKED;
    }

    @Override
    protected String extractUserId(AccountLockedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(AccountLockedEvent event) {
        return "account-locked";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(AccountLockedEvent event) {
        LocalDateTime lockedAt = event.getLockedAt() != null
                ? event.getLockedAt()
                : LocalDateTime.now();

        LocalDateTime unlockAt = event.getUnlockAt() != null
                ? event.getUnlockAt()
                : LocalDateTime.now().plusHours(1);

        var builder = contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withSecurityDetails(event.getFailedAttempts(), event.getIpAddress(), null)
                .withLockDetails(lockedAt, unlockAt)
                .withSystemConstants();

        // Add unlock URL if token is provided
        if (event.getUnlockToken() != null) {
            builder.withAccountUnlock(event.getUnlockToken());
        }

        return builder.build();
    }

    @Override
    protected Map<String, Object> buildMetadata(AccountLockedEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("failedAttempts", event.getFailedAttempts());
        metadata.put("lockedAt", event.getLockedAt());
        return metadata;
    }

    @Override
    protected String resolveLocale(AccountLockedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(AccountLockedEvent event) {
        return NotificationType.SECURITY;
    }

    @Override
    protected NotificationPriority getPriority(AccountLockedEvent event) {
        return NotificationPriority.HIGH;
    }

    @Override
    protected boolean isCritical() {
        return true; // Security alert - send via multiple channels
    }
}
