package com.zote.notification.service.infrastructure.adapters.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zote.kafka.adapter.event.*;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Deserializes Kafka DataEvent to specific DomainEvent classes
 * Uses EventType enum for type-safe event handling
 */
@Component
@Slf4j
public class UserEventDeserializer {

    private final ObjectMapper objectMapper;

    public UserEventDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convert DataEvent to specific DomainEvent based on event type
     */
    public DataEvent deserialize(DataEvent dataEvent) {
        if (dataEvent == null) {
            return null;
        }

        EventType eventType = EventType.fromCode(dataEvent.getEventType());
        if (eventType == null) {
            log.warn("Unknown event type: {}", dataEvent.getEventType());
            return null;
        }

        try {
            // Convert DataEvent to JSON node for flexible deserialization
            ObjectNode eventNode = objectMapper.valueToTree(dataEvent);

            return switch (eventType) {
                case USER_CREATED -> objectMapper.treeToValue(eventNode, UserCreatedEvent.class);
                case USER_UPDATED -> objectMapper.treeToValue(eventNode, UserUpdatedEvent.class);
                case USER_ACTIVATED -> objectMapper.treeToValue(eventNode, UserActivatedEvent.class);
                case USER_SUSPENDED -> objectMapper.treeToValue(eventNode, UserSuspendedEvent.class);
                case PASSWORD_CHANGED -> objectMapper.treeToValue(eventNode, PasswordChangedEvent.class);
                case PASSWORD_RESET_REQUESTED -> objectMapper.treeToValue(eventNode, PasswordResetRequestEvent.class);
                case EMAIL_VERIFICATION_REQUESTED -> objectMapper.treeToValue(eventNode, EmailVerificationRequestEvent.class);
                case FAILED_LOGIN -> objectMapper.treeToValue(eventNode, FailedLoginEvent.class);
                case ACCOUNT_LOCKED -> objectMapper.treeToValue(eventNode, AccountLockedEvent.class);
                case TWO_FACTOR_CODE_SENT -> objectMapper.treeToValue(eventNode, TwoFactorCodeEvent.class);
                default -> {
                    log.debug("Event type {} not handled by UserEventDeserializer", eventType);
                    yield null;
                }
            };
        } catch (Exception e) {
            log.error("Failed to deserialize event {} to DomainEvent", dataEvent.getEventType(), e);
            return null;
        }
    }
}

