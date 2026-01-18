package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.common.utils.enums.TwoFacMethod;
import com.zote.kafka.adapter.event.TwoFactorCodeEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.infrastructure.adapters.events.BaseEventHandler;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles TWO_FACTOR_CODE_SENT events.
 *
 * Responsibilities:
 * - Send 2FA verification code via the specified method (EMAIL or SMS)
 * - Include the 6-digit code and expiration time
 * - Provide security context and instructions
 *
 * This is a CRITICAL security notification that must be sent immediately.
 */
@Slf4j
@Component
public class TwoFactorCodeEventHandler extends BaseEventHandler<TwoFactorCodeEvent> {

    private final TemplateContextBuilder contextBuilder;

    public TwoFactorCodeEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.TWO_FACTOR_CODE_SENT;
    }

    @Override
    protected String extractUserId(TwoFactorCodeEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(TwoFactorCodeEvent event) {
        // Template name based on method: "two-factor-code-email" or "two-factor-code-sms"
        TwoFacMethod method = event.getMethod();
        if (method == TwoFacMethod.EMAIL) {
            return "two-factor-code-email";
        } else if (method == TwoFacMethod.SMS) {
            return "two-factor-code-sms";
        } else {
            log.warn("Unknown 2FA method: {}, defaulting to email template", method);
            return "two-factor-code-email";
        }
    }

    @Override
    protected Map<String, Object> buildTemplateContext(TwoFactorCodeEvent event) {
        var builder = contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withUserId(event.getUserId())
                .withLanguage(event.getLanguage())
                .withSystemConstants();

        // Add 2FA code specific context
        Map<String, Object> context = builder.build();
        context.put("verificationCode", event.getCode());
        context.put("expiresInMinutes", event.getExpiresInMinutes());
        context.put("method", event.getMethod() != null ? event.getMethod().name() : "EMAIL");
        
        // Add formatted expiration message
        if (event.getExpiresInMinutes() > 0) {
            context.put("expirationMessage", 
                String.format("This code will expire in %d minute%s.", 
                    event.getExpiresInMinutes(),
                    event.getExpiresInMinutes() != 1 ? "s" : ""));
        } else {
            context.put("expirationMessage", "This code will expire soon.");
        }

        return context;
    }

    @Override
    protected Map<String, Object> buildMetadata(TwoFactorCodeEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("twoFactorMethod", event.getMethod() != null ? event.getMethod().name() : "EMAIL");
        metadata.put("expiresInMinutes", event.getExpiresInMinutes());
        // Note: We don't include the actual code in metadata for security reasons
        return metadata;
    }

    @Override
    protected NotificationChannel getChannel(TwoFactorCodeEvent event) {
        // Map TwoFacMethod to NotificationChannel
        TwoFacMethod method = event.getMethod();
        if (method == null) {
            log.warn("TwoFacMethod is null, defaulting to EMAIL channel");
            return NotificationChannel.EMAIL;
        }
        
        switch (method) {
            case EMAIL:
                return NotificationChannel.EMAIL;
            case SMS:
                return NotificationChannel.SMS;
            default:
                log.warn("Unknown TwoFacMethod: {}, defaulting to EMAIL channel", method);
                return NotificationChannel.EMAIL;
        }
    }

    @Override
    protected String resolveLocale(TwoFactorCodeEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(TwoFactorCodeEvent event) {
        return NotificationType.SECURITY;
    }

    @Override
    protected NotificationPriority getPriority(TwoFactorCodeEvent event) {
        return NotificationPriority.HIGH; // 2FA codes are high priority
    }
}
