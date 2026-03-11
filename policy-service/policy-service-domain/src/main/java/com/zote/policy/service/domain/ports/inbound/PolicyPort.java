package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.models.CancellationRequest;
import com.zote.policy.service.domain.models.ComplianceViolation;
import com.zote.policy.service.domain.models.PolicyAuditLog;
import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.analytics.*;
import com.zote.policy.service.domain.models.data.*;
import com.zote.policy.service.domain.models.data.ReinstatePolicyData;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PolicyPort {

    Policy createPolicyFromQuote(String quoteId);

    Policy issuePolicy(IssuePolicyData data);

    Payment recordPayment(RecordPaymentData data);

    /** Record a failed payment attempt (10.4). */
    Payment recordFailedPayment(RecordFailedPaymentData data);

    /** Mark DUE billing schedules as OVERDUE when past due date (10.5). Returns count marked. */
    int markBillingSchedulesOverdue();

    /** Run payment monitoring: mark overdue schedules (10.6). Returns count marked. */
    int runPaymentMonitoring();

    Policy endorsePolicy(EndorsePolicyData data);

    /**
     * Request an endorsement (7.1). Creates a PENDING request for back-office approval.
     */
    EndorsementRequest requestEndorsement(RequestEndorsementData data);

    /**
     * Approve endorsement request (7.2) and apply changes (7.3, 7.4, 7.5, 7.6).
     */
    Policy approveEndorsement(ApproveEndorsementData data);

    /**
     * Reject endorsement request (7.2, 7.5, 7.6).
     */
    EndorsementRequest rejectEndorsement(RejectEndorsementData data);

    Policy cancelPolicy(CancelPolicyData data);

    /**
     * Request cancellation (9.1). Creates PENDING request for back-office approval.
     */
    CancellationRequest requestCancellation(RequestCancellationData data);

    /**
     * Approve and process cancellation (9.3). Finalizes the cancellation.
     */
    Policy approveCancellationRequest(ApproveCancellationRequestData data);

    /**
     * Reject cancellation request.
     */
    CancellationRequest rejectCancellationRequest(RejectCancellationRequestData data);

    /**
     * Cancel policies for non-payment after grace period (9.7).
     * Returns count of policies cancelled.
     */
    int cancelPoliciesForNonPayment(int gracePeriodDays);

    RenewalResult renewPolicy(RenewPolicyData data);

    /**
     * Accept renewal quote (8.3, 8.4). Creates new policy version and extends the policy term.
     */
    Policy acceptRenewalQuote(AcceptRenewalData data);

    Policy suspendPolicy(SuspendPolicyData data);

    Policy reinstatePolicy(ReinstatePolicyData data);

    Policy findPolicyById(String policyId);

    Policy findByPolicyNumber(String policyNumber);

    /** Full policy details including versions, documents, status history (12.3). */
    Policy findPolicyDetailsById(String policyId);

    /** All versions of a policy, ordered by version number desc (12.4). */
    List<PolicyVersion> getPolicyVersions(String policyId);

    /** Audit trail: all status changes for a policy (12.5). */
    Page<PolicyStatusHistory> getPolicyAuditTrail(String policyId, PageParams pageParams);

    Page<Policy> listPolicies(PolicySearchCriteria criteria, PageParams pageParams);

    Page<Policy> listPoliciesByCustomer(String customerId, PageParams pageParams);

    PolicySummary getCustomerPolicySummary(String customerId);

    List<Policy> getRenewalReminders(int daysAhead);

    /**
     * Auto-generate renewal quotes for expiring policies (8.1).
     * Returns count of quotes generated.
     */
    int generateRenewalQuotesForExpiringPolicies(int daysAhead);

    /**
     * Checks if a suspended policy can be reinstated (e.g. within window, not expired).
     */
    boolean canReinstate(String policyId);

    /**
     * Returns the next unpaid installment due for a policy, if any.
     */
    Optional<BillingSchedule> getNextPaymentDue(String policyId);

    /**
     * Attempt to auto-issue policy if all preconditions are met (13.4).
     * Called when documents verified / payment recorded - keeps policy status consistent.
     * Returns empty if policy cannot be issued yet.
     */
    Optional<Policy> tryAutoIssuePolicyIfEligible(String policyId);

    /** Full audit log for policy (14.4) - status history + policy_audit_log. */
    Page<PolicyAuditLog> getPolicyAuditLog(String policyId, PageParams pageParams);

    /** Compliance violations for policy (14.3, 14.4). */
    Page<ComplianceViolation> getComplianceViolations(String policyId, PageParams pageParams);

    // ---- Analytics (Epic 16) ----

    /** 16.1 - Portfolio snapshot */
    PortfolioSnapshot getPortfolioSnapshot();

    /** 16.2 - Policy KPIs */
    PolicyKpis getPolicyKpis();

    /** 16.3 - Renewal rates over time */
    RenewalRatesSummary getRenewalRates(LocalDate from, LocalDate to, String periodType);

    /** 16.4 - Cancellation rates */
    CancellationRatesSummary getCancellationRates(LocalDate from, LocalDate to, String periodType);

    /** 16.5 - Premium summary by product, branch, or agent */
    PremiumSummary getPremiumSummary(String dimension);

    /** 16.6 - Policy trends over time */
    PolicyTrends getPolicyTrends(LocalDate from, LocalDate to, String periodType);

    /** 16.8 - Publish analytics snapshot event for BI systems */
    void publishAnalyticsSnapshot();
}
