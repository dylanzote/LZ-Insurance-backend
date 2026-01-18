package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.PasswordChangedEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.infrastructure.adapters.events.BaseEventHandler;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Handles PASSWORD_CHANGED events.
 *
 * Responsibilities:
 * - Send security notification about password change
 * - Include change details (timestamp, IP, changed by)
 * - Provide link to account security settings
 */
@Slf4j
@Component
public class PasswordChangedEventHandler extends BaseEventHandler<PasswordChangedEvent> {

    private final TemplateContextBuilder contextBuilder;

    public PasswordChangedEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.PASSWORD_CHANGED;
    }

    @Override
    protected String extractUserId(PasswordChangedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(PasswordChangedEvent event) {
        return "password-changed";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(PasswordChangedEvent event) {
        LocalDateTime changedAt = event.getOccurredAt() != null
                ? event.getOccurredAt()
                : LocalDateTime.now();

        return contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withPasswordChange(changedAt, event.getChangedBy(), event.getIpAddress())
                .withSystemConstants()
                .withFooter()
                .build();
    }

    @Override
    protected String resolveLocale(PasswordChangedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(PasswordChangedEvent event) {
        return NotificationType.SECURITY;
    }

    @Override
    protected NotificationPriority getPriority(PasswordChangedEvent event) {
        return NotificationPriority.HIGH;
    }
}
