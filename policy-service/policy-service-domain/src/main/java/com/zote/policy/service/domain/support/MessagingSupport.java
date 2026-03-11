package com.zote.policy.service.domain.support;

import com.zote.kafka.adapter.event.policy.EndorsementRejectedEvent;
import com.zote.kafka.adapter.event.policy.EndorsementRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyEndorsedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;
import com.zote.kafka.adapter.event.payment.PaymentFailedEvent;
import com.zote.kafka.adapter.event.payment.PaymentOverdueEvent;
import com.zote.kafka.adapter.event.payment.PaymentRecordedEvent;
import com.zote.kafka.adapter.event.quote.QuoteAcceptedEvent;
import com.zote.kafka.adapter.event.quote.QuoteCreatedEvent;
import com.zote.kafka.adapter.event.quote.QuoteRejectedEvent;
import com.zote.kafka.adapter.event.quote.UnderwritingDecisionEvent;
import com.zote.kafka.adapter.event.policy.CancellationRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCancelledEvent;
import com.zote.kafka.adapter.event.policy.PolicyRenewedEvent;
import com.zote.kafka.adapter.event.document.DocumentDeletedEvent;
import com.zote.kafka.adapter.event.document.DocumentRejectedEvent;
import com.zote.kafka.adapter.event.document.DocumentUploadedEvent;
import com.zote.kafka.adapter.event.analytics.AnalyticsPortfolioSnapshotEvent;
import com.zote.kafka.adapter.event.compliance.ComplianceViolationDetectedEvent;
import com.zote.kafka.adapter.event.document.DocumentVerifiedEvent;
import com.zote.kafka.adapter.event.policy.RenewalQuoteCreatedEvent;
import com.zote.policy.service.domain.models.CancellationRequest;
import com.zote.policy.service.domain.models.EndorsementRequest;
import com.zote.policy.service.domain.models.data.CancelPolicyData;
import com.zote.policy.service.domain.models.Endorsement;
import com.zote.policy.service.domain.models.Payment;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.analytics.PortfolioSnapshot;
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
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
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
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .build();

            eventPublisher.publishPolicyIssuedEvent(event);
            log.info("Published Policy_ISSUED event for  customer: {}", policy.getId());
        } catch (Exception e) {
            log.error("Failed to publish Policy_ISSUED event for customer: {}", policy.getId(), e);
        }
    }

    public void publishQuoteCreatedEvent(Quote quote) {
        try {
            QuoteCreatedEvent event = QuoteCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .quoteId(quote.getId())
                    .quoteNumber(quote.getQuoteNumber())
                    .customerId(quote.getCustomerId())
                    .productId(quote.getProductId())
                    .build();
            eventPublisher.publishQuoteCreated(event);
            log.info("Published QUOTE_CREATED event for quote: {}", quote.getQuoteNumber());
        } catch (Exception e) {
            log.error("Failed to publish QUOTE_CREATED event for quote: {}", quote.getQuoteNumber(), e);
        }
    }

    public void publishQuoteAcceptedEvent(Quote quote) {
        try {
            QuoteAcceptedEvent event = QuoteAcceptedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .quoteId(quote.getId())
                    .quoteNumber(quote.getQuoteNumber())
                    .customerId(quote.getCustomerId())
                    .build();
            eventPublisher.publishQuoteAccepted(event);
            log.info("Published QUOTE_ACCEPTED event for quote: {}", quote.getQuoteNumber());
        } catch (Exception e) {
            log.error("Failed to publish QUOTE_ACCEPTED event for quote: {}", quote.getQuoteNumber(), e);
        }
    }

    public void publishUnderwritingDecisionEvent(Quote quote) {
        try {
            UnderwritingDecisionEvent event = UnderwritingDecisionEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .quoteId(quote.getId())
                    .quoteNumber(quote.getQuoteNumber())
                    .customerId(quote.getCustomerId())
                    .decision(quote.getUnderwritingDecision() != null ? quote.getUnderwritingDecision().name() : null)
                    .reason(quote.getUnderwritingReason())
                    .decidedBy(quote.getUnderwritingDecidedBy())
                    .build();
            eventPublisher.publishUnderwritingDecision(event);
            log.info("Published UNDERWRITING_DECISION event for quote: {}", quote.getQuoteNumber());
        } catch (Exception e) {
            log.error("Failed to publish UNDERWRITING_DECISION event for quote: {}", quote.getQuoteNumber(), e);
        }
    }

    public void publishQuoteRejectedEvent(Quote quote, String reason) {
        try {
            QuoteRejectedEvent event = QuoteRejectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .quoteId(quote.getId())
                    .quoteNumber(quote.getQuoteNumber())
                    .customerId(quote.getCustomerId())
                    .reason(reason)
                    .build();
            eventPublisher.publishQuoteRejected(event);
            log.info("Published QUOTE_REJECTED event for quote: {}", quote.getQuoteNumber());
        } catch (Exception e) {
            log.error("Failed to publish QUOTE_REJECTED event for quote: {}", quote.getQuoteNumber(), e);
        }
    }

    public void publishPaymentRecordedEvent(Payment payment) {
        try {
            PaymentRecordedEvent event = PaymentRecordedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .paymentId(payment.getId())
                    .policyId(payment.getPolicyId())
                    .amount(payment.getAmount())
                    .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                    .paymentDate(payment.getPaymentDate())
                    .transactionId(payment.getTransactionId())
                    .installmentNo(payment.getInstallmentNo())
                    .build();
            eventPublisher.publishPaymentRecorded(event);
            log.info("Published PAYMENT_RECORDED event for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Failed to publish PAYMENT_RECORDED event for payment: {}", payment.getId(), e);
        }
    }

    public void publishPaymentFailedEvent(Payment payment, Policy policy) {
        try {
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .paymentId(payment.getId())
                    .policyId(payment.getPolicyId())
                    .policyNumber(policy != null ? policy.getPolicyNumber() : null)
                    .customerId(policy != null ? policy.getCustomerId() : null)
                    .amount(payment.getAmount())
                    .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                    .attemptedDate(payment.getPaymentDate())
                    .failureReason(payment.getFailureReason())
                    .transactionId(payment.getTransactionId())
                    .installmentNo(payment.getInstallmentNo())
                    .build();
            eventPublisher.publishPaymentFailed(event);
            log.info("Published PAYMENT_FAILED event for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Failed to publish PAYMENT_FAILED event for payment: {}", payment.getId(), e);
        }
    }

    public void publishPaymentOverdueEvent(Policy policy, int overdueCount, java.math.BigDecimal totalOverdue, java.time.LocalDate nextDue) {
        try {
            PaymentOverdueEvent event = PaymentOverdueEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .overdueInstallmentCount(overdueCount)
                    .totalOverdueAmount(totalOverdue)
                    .nextDueDate(nextDue)
                    .build();
            eventPublisher.publishPaymentOverdue(event);
            log.info("Published PAYMENT_OVERDUE event for policy: {}", policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish PAYMENT_OVERDUE event for policy: {}", policy.getPolicyNumber(), e);
        }
    }

    public void publishDocumentUploadedEvent(Policy policy, PolicyDocument doc) {
        try {
            DocumentUploadedEvent event = DocumentUploadedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .documentId(doc.getId())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .documentType(doc.getType() != null ? doc.getType().name() : null)
                    .documentName(doc.getName())
                    .status(doc.getStatus() != null ? doc.getStatus().name() : null)
                    .build();
            eventPublisher.publishDocumentUploaded(event);
            log.info("Published DOCUMENT_UPLOADED event for document: {}", doc.getId());
        } catch (Exception e) {
            log.error("Failed to publish DOCUMENT_UPLOADED event for document: {}", doc.getId(), e);
        }
    }

    public void publishDocumentVerifiedEvent(Policy policy, PolicyDocument doc) {
        try {
            DocumentVerifiedEvent event = DocumentVerifiedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .documentId(doc.getId())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .documentType(doc.getType() != null ? doc.getType().name() : null)
                    .verifiedBy(doc.getVerifiedBy())
                    .build();
            eventPublisher.publishDocumentVerified(event);
            log.info("Published DOCUMENT_VERIFIED event for document: {}", doc.getId());
        } catch (Exception e) {
            log.error("Failed to publish DOCUMENT_VERIFIED event for document: {}", doc.getId(), e);
        }
    }

    public void publishDocumentRejectedEvent(Policy policy, PolicyDocument doc) {
        try {
            DocumentRejectedEvent event = DocumentRejectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .documentId(doc.getId())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .documentType(doc.getType() != null ? doc.getType().name() : null)
                    .rejectionReason(doc.getRejectionReason())
                    .build();
            eventPublisher.publishDocumentRejected(event);
            log.info("Published DOCUMENT_REJECTED event for document: {}", doc.getId());
        } catch (Exception e) {
            log.error("Failed to publish DOCUMENT_REJECTED event for document: {}", doc.getId(), e);
        }
    }

    public void publishDocumentDeletedEvent(Policy policy, PolicyDocument doc) {
        try {
            DocumentDeletedEvent event = DocumentDeletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .documentId(doc.getId())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .documentType(doc.getType() != null ? doc.getType().name() : null)
                    .build();
            eventPublisher.publishDocumentDeleted(event);
            log.info("Published DOCUMENT_DELETED event for document: {}", doc.getId());
        } catch (Exception e) {
            log.error("Failed to publish DOCUMENT_DELETED event for document: {}", doc.getId(), e);
        }
    }

    public void publishPolicyEndorsedEvent(Endorsement endorsement) {
        try {
            PolicyEndorsedEvent event = PolicyEndorsedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .policyId(endorsement.getPolicyId())
                    .endorsementId(endorsement.getId())
                    .endorsementType(endorsement.getType() != null ? endorsement.getType().name() : null)
                    .premiumChange(endorsement.getPremiumChange())
                    .build();
            eventPublisher.publishPolicyEndorsed(event);
            log.info("Published POLICY_ENDORSED event for policy: {}", endorsement.getPolicyId());
        } catch (Exception e) {
            log.error("Failed to publish POLICY_ENDORSED event for policy: {}", endorsement.getPolicyId(), e);
        }
    }

    public void publishEndorsementRequestedEvent(EndorsementRequest request) {
        try {
            EndorsementRequestedEvent event = EndorsementRequestedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .endorsementRequestId(request.getId())
                    .policyId(request.getPolicyId())
                    .endorsementType(request.getType() != null ? request.getType().name() : null)
                    .requestedBy(request.getRequestedBy())
                    .build();
            eventPublisher.publishEndorsementRequested(event);
            log.info("Published ENDORSEMENT_REQUESTED event for request: {}", request.getId());
        } catch (Exception e) {
            log.error("Failed to publish ENDORSEMENT_REQUESTED event for request: {}", request.getId(), e);
        }
    }

    public void publishEndorsementRejectedEvent(EndorsementRequest request) {
        try {
            EndorsementRejectedEvent event = EndorsementRejectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .endorsementRequestId(request.getId())
                    .policyId(request.getPolicyId())
                    .decidedBy(request.getDecidedBy())
                    .rejectionReason(request.getRejectionReason())
                    .build();
            eventPublisher.publishEndorsementRejected(event);
            log.info("Published ENDORSEMENT_REJECTED event for request: {}", request.getId());
        } catch (Exception e) {
            log.error("Failed to publish ENDORSEMENT_REJECTED event for request: {}", request.getId(), e);
        }
    }

    public void publishPolicyCancelledEvent(Policy policy, CancelPolicyData data) {
        try {
            PolicyCancelledEvent event = PolicyCancelledEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .cancellationType(data != null && data.getCancellationType() != null ? data.getCancellationType().name() : null)
                    .reason(data != null ? data.getReason() : null)
                    .cancelledBy(data != null ? data.getCancelledBy() : null)
                    .refundAmount(data != null ? data.getRefundAmount() : null)
                    .build();
            eventPublisher.publishPolicyCancelled(event);
            log.info("Published POLICY_CANCELLED event for policy: {}", policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish POLICY_CANCELLED event for policy: {}", policy.getPolicyNumber(), e);
        }
    }

    public void publishCancellationRequestedEvent(Policy policy, CancellationRequest request) {
        try {
            CancellationRequestedEvent event = CancellationRequestedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .cancellationRequestId(request.getId())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .cancellationType(request.getCancellationType() != null ? request.getCancellationType().name() : null)
                    .reason(request.getReason())
                    .requestedBy(request.getRequestedBy())
                    .build();
            eventPublisher.publishCancellationRequested(event);
            log.info("Published CANCELLATION_REQUESTED event for policy: {}", policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish CANCELLATION_REQUESTED event for policy: {}", policy.getPolicyNumber(), e);
        }
    }

    public void publishRenewalQuoteCreatedEvent(Policy policy, Quote quote) {
        try {
            RenewalQuoteCreatedEvent event = RenewalQuoteCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .quoteId(quote.getId())
                    .quoteNumber(quote.getQuoteNumber())
                    .customerId(policy.getCustomerId())
                    .newEffectiveDate(quote.getEffectiveDate())
                    .newExpiryDate(quote.getExpiryDate())
                    .renewalPremium(quote.getPremiumTotal())
                    .build();
            eventPublisher.publishRenewalQuoteCreated(event);
            log.info("Published RENEWAL_QUOTE_CREATED event for policy: {}", policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish RENEWAL_QUOTE_CREATED event for policy: {}", policy.getPolicyNumber(), e);
        }
    }

    public void publishPolicyRenewedEvent(Policy policy, com.zote.policy.service.domain.models.Quote renewalQuote) {
        try {
            PolicyRenewedEvent event = PolicyRenewedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .policyId(policy.getId())
                    .policyNumber(policy.getPolicyNumber())
                    .customerId(policy.getCustomerId())
                    .renewalQuoteId(renewalQuote.getId())
                    .newEffectiveDate(policy.getEffectiveDate())
                    .newExpiryDate(policy.getExpiryDate())
                    .renewalPremium(renewalQuote.getPremiumTotal())
                    .build();
            eventPublisher.publishPolicyRenewed(event);
            log.info("Published POLICY_RENEWED event for policy: {}", policy.getPolicyNumber());
        } catch (Exception e) {
            log.error("Failed to publish POLICY_RENEWED event for policy: {}", policy.getPolicyNumber(), e);
        }
    }

    public void publishComplianceViolationDetectedEvent(com.zote.policy.service.domain.models.ComplianceViolation violation) {
        try {
            ComplianceViolationDetectedEvent event = ComplianceViolationDetectedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(violation.getDetectedAt())
                    .correlationId(UUID.randomUUID().toString())
                    .violationId(violation.getId())
                    .policyId(violation.getPolicyId())
                    .violationType(violation.getViolationType())
                    .description(violation.getDescription())
                    .severity(violation.getSeverity())
                    .metadata(violation.getMetadata())
                    .build();
            eventPublisher.publishComplianceViolationDetected(event);
            log.info("Published COMPLIANCE_VIOLATION_DETECTED event for policy: {}", violation.getPolicyId());
        } catch (Exception e) {
            log.error("Failed to publish COMPLIANCE_VIOLATION_DETECTED event for policy: {}", violation.getPolicyId(), e);
        }
    }

    /** 16.8 - Publish analytics portfolio snapshot for BI systems */
    public void publishAnalyticsPortfolioSnapshotEvent(PortfolioSnapshot snapshot) {
        try {
            AnalyticsPortfolioSnapshotEvent event = AnalyticsPortfolioSnapshotEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .occurredAt(LocalDateTime.now())
                    .correlationId(UUID.randomUUID().toString())
                    .totalPolicies(snapshot.getTotalPolicies())
                    .activePolicies(snapshot.getActivePolicies())
                    .expiredPolicies(snapshot.getExpiredPolicies())
                    .cancelledPolicies(snapshot.getCancelledPolicies())
                    .suspendedPolicies(snapshot.getSuspendedPolicies())
                    .pendingPolicies(snapshot.getPendingPolicies())
                    .countByStatus(snapshot.getCountByStatus())
                    .totalPremium(snapshot.getTotalPremium())
                    .build();
            eventPublisher.publishAnalyticsPortfolioSnapshot(event);
            log.info("Published ANALYTICS_PORTFOLIO_SNAPSHOT event");
        } catch (Exception e) {
            log.error("Failed to publish ANALYTICS_PORTFOLIO_SNAPSHOT event", e);
        }
    }
}
