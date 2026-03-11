package com.zote.policy.service.infrastructure.outbound.event;

import com.zote.kafka.adapter.event.policy.EndorsementRejectedEvent;
import com.zote.kafka.adapter.event.policy.EndorsementRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.CancellationRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCancelledEvent;
import com.zote.kafka.adapter.event.policy.PolicyRenewedEvent;
import com.zote.kafka.adapter.event.policy.RenewalQuoteCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyEndorsedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;
import com.zote.kafka.adapter.event.payment.PaymentFailedEvent;
import com.zote.kafka.adapter.event.payment.PaymentOverdueEvent;
import com.zote.kafka.adapter.event.document.DocumentDeletedEvent;
import com.zote.kafka.adapter.event.document.DocumentRejectedEvent;
import com.zote.kafka.adapter.event.analytics.AnalyticsPortfolioSnapshotEvent;
import com.zote.kafka.adapter.event.compliance.ComplianceViolationDetectedEvent;
import com.zote.kafka.adapter.event.document.DocumentUploadedEvent;
import com.zote.kafka.adapter.event.document.DocumentVerifiedEvent;
import com.zote.kafka.adapter.event.payment.PaymentRecordedEvent;
import com.zote.kafka.adapter.event.quote.QuoteAcceptedEvent;
import com.zote.kafka.adapter.event.quote.QuoteCreatedEvent;
import com.zote.kafka.adapter.event.quote.QuoteRejectedEvent;
import com.zote.kafka.adapter.event.quote.UnderwritingDecisionEvent;
import com.zote.kafka.adapter.models.DataEvent;
import com.zote.kafka.adapter.models.EventType;
import com.zote.kafka.adapter.service.EnhancedMessageProducer;
import com.zote.policy.service.domain.ports.outbound.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private final EnhancedMessageProducer messageProducer;

    @Value("${policy-service.kafka.topic:policy-events}")
    private String policyEventsTopic;

    @Value("${policy-service.kafka.quote-topic:quote-events}")
    private String quoteEventsTopic;

    @Value("${policy-service.kafka.payment-topic:payment-events}")
    private String paymentEventsTopic;

    @Override
    public void publishPolicyCreated(PolicyCreatedEvent event) {
        publishEvent(event, EventType.POLICY_CREATED);
    }

    @Override
    public void publishPolicyIssuedEvent(PolicyIssuedEvent event) {
        publishEvent(event, EventType.POLICY_ISSUED);
    }

    @Override
    public void publishPolicyEndorsed(PolicyEndorsedEvent event) {
        publishEvent(event, EventType.POLICY_ENDORSED);
    }

    @Override
    public void publishEndorsementRequested(EndorsementRequestedEvent event) {
        publishEvent(event, EventType.ENDORSEMENT_REQUESTED);
    }

    @Override
    public void publishEndorsementRejected(EndorsementRejectedEvent event) {
        publishEvent(event, EventType.ENDORSEMENT_REJECTED);
    }

    @Override
    public void publishPolicyCancelled(PolicyCancelledEvent event) {
        publishEvent(event, EventType.POLICY_CANCELLED);
    }

    @Override
    public void publishCancellationRequested(CancellationRequestedEvent event) {
        publishEvent(event, EventType.CANCELLATION_REQUESTED);
    }

    @Override
    public void publishRenewalQuoteCreated(RenewalQuoteCreatedEvent event) {
        publishEvent(event, EventType.RENEWAL_QUOTE_CREATED);
    }

    @Override
    public void publishPolicyRenewed(PolicyRenewedEvent event) {
        publishEvent(event, EventType.POLICY_RENEWED);
    }

    @Override
    public void publishQuoteCreated(QuoteCreatedEvent event) {
        publishQuoteEvent(event, EventType.QUOTE_CREATED);
    }

    @Override
    public void publishQuoteAccepted(QuoteAcceptedEvent event) {
        publishQuoteEvent(event, EventType.QUOTE_ACCEPTED);
    }

    @Override
    public void publishQuoteRejected(QuoteRejectedEvent event) {
        publishQuoteEvent(event, EventType.QUOTE_REJECTED);
    }

    @Override
    public void publishUnderwritingDecision(UnderwritingDecisionEvent event) {
        publishQuoteEvent(event, EventType.UNDERWRITING_DECISION);
    }

    @Override
    public void publishPaymentRecorded(PaymentRecordedEvent event) {
        publishPaymentEvent(event, EventType.PAYMENT_RECORDED);
    }

    @Override
    public void publishPaymentFailed(PaymentFailedEvent event) {
        publishPaymentEvent(event, EventType.PAYMENT_FAILED);
    }

    @Override
    public void publishPaymentOverdue(PaymentOverdueEvent event) {
        publishPaymentEvent(event, EventType.PAYMENT_OVERDUE);
    }

    @Override
    public void publishDocumentUploaded(DocumentUploadedEvent event) {
        publishEvent(event, EventType.DOCUMENT_UPLOADED);
    }

    @Override
    public void publishDocumentVerified(DocumentVerifiedEvent event) {
        publishEvent(event, EventType.DOCUMENT_VERIFIED);
    }

    @Override
    public void publishDocumentRejected(DocumentRejectedEvent event) {
        publishEvent(event, EventType.DOCUMENT_REJECTED);
    }

    @Override
    public void publishDocumentDeleted(DocumentDeletedEvent event) {
        publishEvent(event, EventType.DOCUMENT_DELETED);
    }

    @Override
    public void publishComplianceViolationDetected(ComplianceViolationDetectedEvent event) {
        publishEvent(event, EventType.COMPLIANCE_VIOLATION_DETECTED);
    }

    @Override
    public void publishAnalyticsPortfolioSnapshot(AnalyticsPortfolioSnapshotEvent event) {
        publishEvent(event, EventType.ANALYTICS_PORTFOLIO_SNAPSHOT);
    }

    private void publishPaymentEvent(com.zote.kafka.adapter.models.DataEvent event, EventType eventType) {
        try {
            log.info("Publishing {} event to Kafka topic: {}", eventType.getDescription(), paymentEventsTopic);
            messageProducer.sendMessageAsync(paymentEventsTopic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId());
                        } else {
                            log.error("Failed to publish {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing {} event to Kafka: {}", eventType.getCode(), e.getMessage(), e);
        }
    }

    private void publishQuoteEvent(DataEvent event, EventType eventType) {
        try {
            log.info("Publishing {} event to Kafka topic: {}", eventType.getDescription(), quoteEventsTopic);
            messageProducer.sendMessageAsync(quoteEventsTopic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId());
                        } else {
                            log.error("Failed to publish {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing {} event to Kafka: {}", eventType.getCode(), e.getMessage(), e);
        }
    }

    private void publishEvent(DataEvent event, EventType eventType) {
        try {
            log.info("Publishing {} event to Kafka topic: {}", eventType.getDescription(), policyEventsTopic);
            messageProducer.sendMessageAsync(policyEventsTopic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId());
                        } else {
                            log.error("Failed to publish {} event (ID: {}) to Kafka",
                                    eventType.getCode(), event.getEventId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error publishing {} event to Kafka: {}", eventType.getCode(), e.getMessage(), e);
        }
    }
}
