package com.zote.policy.service.domain.ports.outbound;

import com.zote.kafka.adapter.event.policy.EndorsementRejectedEvent;
import com.zote.kafka.adapter.event.policy.EndorsementRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCreatedEvent;
import com.zote.kafka.adapter.event.policy.CancellationRequestedEvent;
import com.zote.kafka.adapter.event.policy.PolicyCancelledEvent;
import com.zote.kafka.adapter.event.policy.PolicyEndorsedEvent;
import com.zote.kafka.adapter.event.policy.RenewalQuoteCreatedEvent;
import com.zote.kafka.adapter.event.policy.PolicyIssuedEvent;
import com.zote.kafka.adapter.event.payment.PaymentFailedEvent;
import com.zote.kafka.adapter.event.payment.PaymentOverdueEvent;
import com.zote.kafka.adapter.event.payment.PaymentRecordedEvent;
import com.zote.kafka.adapter.event.quote.QuoteAcceptedEvent;
import com.zote.kafka.adapter.event.quote.QuoteCreatedEvent;
import com.zote.kafka.adapter.event.quote.QuoteRejectedEvent;
import com.zote.kafka.adapter.event.document.DocumentDeletedEvent;
import com.zote.kafka.adapter.event.document.DocumentRejectedEvent;
import com.zote.kafka.adapter.event.document.DocumentUploadedEvent;
import com.zote.kafka.adapter.event.analytics.AnalyticsPortfolioSnapshotEvent;
import com.zote.kafka.adapter.event.compliance.ComplianceViolationDetectedEvent;
import com.zote.kafka.adapter.event.document.DocumentVerifiedEvent;
import com.zote.kafka.adapter.event.quote.UnderwritingDecisionEvent;

public interface EventPublisherPort {

    void publishPolicyCreated(PolicyCreatedEvent event);

    void publishPolicyIssuedEvent(PolicyIssuedEvent event);

    void publishPolicyEndorsed(PolicyEndorsedEvent event);

    void publishEndorsementRequested(EndorsementRequestedEvent event);

    void publishEndorsementRejected(EndorsementRejectedEvent event);

    void publishPolicyCancelled(PolicyCancelledEvent event);

    void publishCancellationRequested(CancellationRequestedEvent event);

    void publishRenewalQuoteCreated(RenewalQuoteCreatedEvent event);

    void publishPolicyRenewed(com.zote.kafka.adapter.event.policy.PolicyRenewedEvent event);

    void publishQuoteCreated(QuoteCreatedEvent event);

    void publishQuoteAccepted(QuoteAcceptedEvent event);

    void publishQuoteRejected(QuoteRejectedEvent event);

    void publishUnderwritingDecision(UnderwritingDecisionEvent event);

    void publishPaymentRecorded(PaymentRecordedEvent event);

    void publishPaymentFailed(PaymentFailedEvent event);

    void publishPaymentOverdue(PaymentOverdueEvent event);

    void publishDocumentUploaded(DocumentUploadedEvent event);
    void publishDocumentVerified(DocumentVerifiedEvent event);
    void publishDocumentRejected(DocumentRejectedEvent event);
    void publishDocumentDeleted(DocumentDeletedEvent event);

    void publishComplianceViolationDetected(ComplianceViolationDetectedEvent event);

    /** 16.8 - Publish analytics event for BI systems */
    void publishAnalyticsPortfolioSnapshot(AnalyticsPortfolioSnapshotEvent event);
}
