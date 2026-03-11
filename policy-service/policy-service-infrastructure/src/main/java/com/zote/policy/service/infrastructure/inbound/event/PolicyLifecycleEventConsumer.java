package com.zote.policy.service.infrastructure.inbound.event;

import com.zote.kafka.adapter.event.document.DocumentVerifiedEvent;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Event-driven policy lifecycle updates (13.4).
 * Reacts to domain events to keep policy status consistent.
 * Enabled when policy.automation.event-driven.enabled=true (for multi-service deployments).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyLifecycleEventConsumer {

    private final PolicyPort policyPort;

    @Value("${policy.automation.event-driven.enabled:false}")
    private boolean eventDrivenEnabled;

    @KafkaListener(
            topics = "${policy-service.kafka.topic:policy-events}",
            groupId = "${spring.application.name:policy-service}-lifecycle-consumer"
    )
    public void onPolicyEvent(DataEvent event) {
        if (!eventDrivenEnabled) {
            return;
        }
        try {
            if (EventType.DOCUMENT_VERIFIED.getCode().equals(event.getEventType()) && event instanceof DocumentVerifiedEvent docEvent) {
                handleDocumentVerified(docEvent);
            }
        } catch (Exception e) {
            log.warn("Failed to process policy lifecycle event {}: {}", event.getEventType(), e.getMessage());
        }
    }

    private void handleDocumentVerified(DocumentVerifiedEvent event) {
        if (event.getPolicyId() == null || event.getPolicyId().isBlank()) {
            return;
        }
        log.debug("Received DocumentVerifiedEvent for policy {} - checking if eligible for auto-issue", event.getPolicyId());
        policyPort.tryAutoIssuePolicyIfEligible(event.getPolicyId());
    }
}
