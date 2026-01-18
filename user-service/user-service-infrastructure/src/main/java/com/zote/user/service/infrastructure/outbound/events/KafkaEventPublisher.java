package com.zote.user.service.infrastructure.outbound.events;

import com.zote.kafka.adapter.event.*;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.kafka.adapter.service.EnhancedMessageProducer;
import com.zote.user.service.domain.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Kafka implementation of EventPublisherPort
 * Publishes user domain events to Kafka using the Kafka adapter
 * Uses centralized EventType enum for type-safe event handling
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private final EnhancedMessageProducer messageProducer;

    @Value("${user-service.kafka.topic:user-events}")
    private String userEventsTopic;

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        publishEvent(event, EventType.USER_CREATED);
    }

    @Override
    public void publishUserUpdated(UserUpdatedEvent event) {
        publishEvent(event, EventType.USER_UPDATED);
    }

    @Override
    public void publishUserActivated(UserActivatedEvent event) {
        publishEvent(event, EventType.USER_ACTIVATED);
    }

    @Override
    public void publishUserSuspended(UserSuspendedEvent event) {
        publishEvent(event, EventType.USER_SUSPENDED);
    }

    @Override
    public void publishPasswordChanged(PasswordChangedEvent event) {
        publishEvent(event, EventType.PASSWORD_CHANGED);
    }

    @Override
    public void publishPasswordResetRequest(PasswordResetRequestEvent event) {
        publishEvent(event, EventType.PASSWORD_RESET_REQUESTED);
    }

    @Override
    public void publishEmailVerificationRequest(EmailVerificationRequestEvent event) {
        publishEvent(event, EventType.EMAIL_VERIFICATION_REQUESTED);
    }

    @Override
    public void publishFailedLogin(FailedLoginEvent event) {
        publishEvent(event, EventType.FAILED_LOGIN);
    }

    @Override
    public void publishAccountLocked(AccountLockedEvent event) {
        publishEvent(event, EventType.ACCOUNT_LOCKED);
    }

    @Override
    public void publishTwoFactorCode(TwoFactorCodeEvent event) {
        publishEvent(event, EventType.TWO_FACTOR_CODE_SENT);
    }

    private void publishEvent(DataEvent event, EventType eventType) {
        try {
            log.info("Publishing {} event to Kafka topic: {}", eventType.getDescription(), userEventsTopic);
            messageProducer.sendMessageAsync(userEventsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Successfully published {} event (ID: {}) to Kafka", eventType.getCode(), event.getEventId());
                    } else {
                        log.error("Failed to publish {} event (ID: {}) to Kafka", eventType.getCode(), event.getEventId(), ex);
                    }
                });
        } catch (Exception e) {
            log.error("Error publishing {} event to Kafka: {}", eventType.getCode(), e.getMessage(), e);
            // In production, consider using a dead letter queue or retry mechanism
        }
    }
}

