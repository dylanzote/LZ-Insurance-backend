package com.zote.policy.service.domain.support;

import com.zote.kafka.adapter.event.UserCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingSupport {

    private final EventPublisherPort eventPublisher;

    public void publishPolicyCreatedEvent(Policy policy) {
        try {
            PolicyCreatedEvent event = PolicyCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .build();

            eventPublisher.publishPolicyCreated(event);
            log.info("Published Policy_CREATED event for self-registered user: {}", policy.getId());
        } catch (Exception e) {
            log.error("Failed to publish Policy_CREATED event for user: {}", policy.getId(), e);
        }
    }

    public void publishPolicyIssuedEvent(Policy policy) {
        try {
            PolicyIssuedEvent event = PolicyIssuedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .build();

            eventPublisher.publishPolicyIssuedEvent(event);
            log.info("Published Policy_ISSUED event for  customer: {}", policy.getId());
        } catch (Exception e) {
            log.error("Failed to publish Policy_ISSUED event for customer: {}", policy.getId(), e);
        }
    }
}
