package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.kafka.adapter.event.UserCreatedEvent;
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
public class UserCreatedEventHandler extends BaseEventHandler<UserCreatedEvent> {

    private final TemplateContextBuilder contextBuilder;

    public UserCreatedEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_CREATED;
    }

    @Override
    protected String extractUserId(UserCreatedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(UserCreatedEvent event) {
        return event.isCreatedByAdmin() ? "admin-created-user" : "welcome-customer";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(UserCreatedEvent event) {
        var builder = contextBuilder.newContext()
                .withUser(event.getFirstName(), event.getLastName(), event.getEmail())
                .withUserId(event.getUserId())
                .withLanguage(event.getLanguage())
                .withOptionalDetails(event.getPhoneNumber(), event.getBranchId(), event.getDepartment())
                .withSystemConstants();

        // Add password reset for admin-created users (initial password setup)
        // Uses /set-password path on admin portal
        if (event.isCreatedByAdmin() && event.getPasswordResetToken() != null) {
            builder.withPasswordReset(
                event.getPasswordResetToken(),
                event.getPasswordResetTokenExpiryTime()
            ).withFooter();
        }

        return builder.build();
    }

    @Override
    protected Map<String, Object> buildMetadata(UserCreatedEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));

        if (event.isCreatedByAdmin()) {
            metadata.put("createdByAdmin", true);
            metadata.put("createdByUserId", event.getCreatedByUserId());
        }

        return metadata;
    }

    @Override
    protected String resolveLocale(UserCreatedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationPriority getPriority(UserCreatedEvent event) {
        return NotificationPriority.HIGH;
    }
}
