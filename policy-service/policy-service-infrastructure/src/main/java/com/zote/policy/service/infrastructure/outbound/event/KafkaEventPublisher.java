package com.zote.policy.service.infrastructure.outbound.event;

import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;
import com.zote.policy.service.domain.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {
    @Override
    public void publishPolicyCreated(PolicyCreatedEvent event) {

    }

    @Override
    public void publishPolicyIssuedEvent(PolicyIssuedEvent event) {

    }
}
