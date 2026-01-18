package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.kafka.adapter.event.PasswordResetRequestEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.infrastructure.adapters.events.BaseEventHandler;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class PasswordResetRequestEventHandler extends BaseEventHandler<PasswordResetRequestEvent> {

    private final TemplateContextBuilder contextBuilder;

    public PasswordResetRequestEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.PASSWORD_RESET_REQUESTED;
    }

    @Override
    protected String extractUserId(PasswordResetRequestEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(PasswordResetRequestEvent event) {
        return "password-reset-request";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(PasswordResetRequestEvent event) {
        return contextBuilder.newContext()
                .withUser(event.getFirstName(), event.getLastName(), event.getEmail())
                .withUserId(event.getUserId())
                .withLanguage(event.getLanguage())
                .withPasswordResetRequest(event.getPasswordResetToken(), event.getPasswordResetTokenExpiryTime(), event.isCustomer())
                .withSystemConstants()
                .withFooter()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(PasswordResetRequestEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("tokenExpiryHours", event.getPasswordResetTokenExpiryTime());
        // Note: We don't include the actual token in metadata for security reasons
        return metadata;
    }

    @Override
    protected String resolveLocale(PasswordResetRequestEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationPriority getPriority(PasswordResetRequestEvent event) {
        return NotificationPriority.HIGH; // Password reset is high priority
    }
}

