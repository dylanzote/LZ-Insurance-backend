package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.kafka.adapter.event.EmailVerificationRequestEvent;
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
public class EmailVerificationRequestEventHandler extends BaseEventHandler<EmailVerificationRequestEvent> {

    private final TemplateContextBuilder contextBuilder;

    public EmailVerificationRequestEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.EMAIL_VERIFICATION_REQUESTED;
    }

    @Override
    protected String extractUserId(EmailVerificationRequestEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(EmailVerificationRequestEvent event) {
        return "email-verification-request";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(EmailVerificationRequestEvent event) {
        return contextBuilder.newContext()
                .withUser(event.getFirstName(), event.getLastName(), event.getEmail())
                .withUserId(event.getUserId())
                .withLanguage(event.getLanguage())
                .withEmailVerification(event.getVerificationToken(), event.getTokenExpiryHours(), event.isCustomer())
                .withSystemConstants()
                .withFooter()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(EmailVerificationRequestEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("tokenExpiryHours", event.getTokenExpiryHours());
        // Note: We don't include the actual token in metadata for security reasons
        return metadata;
    }

    @Override
    protected String resolveLocale(EmailVerificationRequestEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationPriority getPriority(EmailVerificationRequestEvent event) {
        return NotificationPriority.HIGH; // Email verification is high priority
    }
}


