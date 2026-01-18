package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.UserUpdatedEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.infrastructure.adapters.events.BaseEventHandler;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles USER_UPDATED events.
 *
 * Responsibilities:
 * - Send notification about account updates
 * - Include updated fields information
 * - Provide account management link
 */
@Slf4j
@Component
public class UserUpdateEventHandler extends BaseEventHandler<UserUpdatedEvent> {

    private final TemplateContextBuilder contextBuilder;

    public UserUpdateEventHandler(SendNotificationPort sendNotificationPort, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_UPDATED;
    }

    @Override
    protected String extractUserId(UserUpdatedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(UserUpdatedEvent event) {
        return "account-updated";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(UserUpdatedEvent event) {
        return contextBuilder.newContext()
                .withUser(event.getFirstName(), event.getLastName(), event.getEmail())
                .withAccountUrl("/profile")
                .with("updatedFields", event.getUpdatedFields())
                .withSystemConstants()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(UserUpdatedEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("email", event.getEmail() != null ? event.getEmail() : "");
        metadata.put("updatedFields", event.getUpdatedFields() != null ? event.getUpdatedFields() : "");
        return metadata;
    }

    @Override
    protected String resolveLocale(UserUpdatedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(UserUpdatedEvent event) {
        return NotificationType.TRANSACTIONAL;
    }

    @Override
    protected NotificationPriority getPriority(UserUpdatedEvent event) {
        return NotificationPriority.MEDIUM;
    }
}
