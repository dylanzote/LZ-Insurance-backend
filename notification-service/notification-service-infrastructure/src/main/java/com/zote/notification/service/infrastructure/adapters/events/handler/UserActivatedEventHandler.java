package com.zote.notification.service.infrastructure.adapters.events.handler;

import com.zote.common.utils.enums.NotificationPriority;
import com.zote.common.utils.enums.NotificationType;
import com.zote.kafka.adapter.event.UserActivatedEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.notification.service.domain.ports.inbound.SendNotificationPort;
import com.zote.notification.service.domain.service.NotificationPreferenceService;
import com.zote.notification.service.domain.usecases.UserValidationService;
import com.zote.notification.service.infrastructure.adapters.events.TemplateContextBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserActivatedEventHandler extends MultiChannelEventHandler<UserActivatedEvent>{

    private final TemplateContextBuilder contextBuilder;

    public UserActivatedEventHandler(SendNotificationPort sendNotificationPort, UserValidationService userValidationService, NotificationPreferenceService notificationPreferenceService, TemplateContextBuilder contextBuilder) {
        super(sendNotificationPort, userValidationService, notificationPreferenceService);
        this.contextBuilder = contextBuilder;
    }

    @Override
    public EventType getEventType() {
        return EventType.USER_ACTIVATED;
    }

    @Override
    protected String extractUserId(UserActivatedEvent event) {
        return event.getUserId();
    }

    @Override
    protected String selectTemplate(UserActivatedEvent event) {
        return "user-activated";
    }

    @Override
    protected Map<String, Object> buildTemplateContext(UserActivatedEvent event) {
        return contextBuilder.newContext()
                .withUser(event.getFirstName(), null, event.getEmail())
                .withAccountUrl("/login")
                .withSystemConstants()
                .withFooter()
                .build();
    }

    @Override
    protected Map<String, Object> buildMetadata(UserActivatedEvent event) {
        Map<String, Object> metadata = new HashMap<>(super.buildMetadata(event));
        metadata.put("activatedBy", event.getActivatedBy() != null ? event.getActivatedBy() : "SYSTEM");
        return metadata;
    }

    @Override
    protected String resolveLocale(UserActivatedEvent event) {
        return event.getLanguage() != null ? event.getLanguage().getCode() : "en";
    }

    @Override
    protected NotificationType getNotificationType(UserActivatedEvent event) {
        return NotificationType.TRANSACTIONAL;
    }

    @Override
    protected NotificationPriority getPriority(UserActivatedEvent event) {
        return NotificationPriority.HIGH;
    }

    @Override
    protected boolean isCritical() {
        return true; // Admin action - send via multiple channels
    }
}
