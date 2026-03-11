package com.zote.policy.service.api.controller;

import com.zote.common.utils.models.Permissions;
import com.zote.policy.service.api.request.*;
import com.zote.policy.service.api.response.BillingScheduleResponse;
import com.zote.policy.service.api.response.EndorsementRequestResponse;
import com.zote.policy.service.api.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Policy API")
@RestController
@RequestMapping("/policy/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface PolicyApi {

    @Operation(summary = "Create policy from accepted quote")
    @PostMapping("create-from-quote")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse createPolicyFromQuote(@Valid @RequestBody CreatePolicyFromQuoteRequest request);

    @Operation(summary = "Issue policy")
    @PostMapping("issue")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse issuePolicy(@Valid @RequestBody IssuePolicyRequest request);

    @Operation(summary = "Record payment")
    @PostMapping("record-payment")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PaymentResponse recordPayment(@Valid @RequestBody RecordPaymentRequest request);

    @Operation(summary = "Record failed payment (10.4)")
    @PostMapping("record-failed-payment")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PaymentResponse recordFailedPayment(@Valid @RequestBody RecordFailedPaymentRequest request);

    @Operation(summary = "Run payment monitoring - mark overdue schedules (10.6)")
    @PostMapping("billing/run-monitoring")
    @RolesAllowed({Permissions.IS_ADMIN})
    int runPaymentMonitoring();

    @Operation(summary = "Endorse policy")
    @PostMapping("endorse")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse endorsePolicy(@Valid @RequestBody EndorsePolicyRequest request);

    @Operation(summary = "Request endorsement (creates PENDING request for approval)")
    @PostMapping("endorsement/request")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    EndorsementRequestResponse requestEndorsement(@Valid @RequestBody RequestEndorsementRequest request);

    @Operation(summary = "Approve endorsement request and apply changes")
    @PostMapping("endorsement/approve")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse approveEndorsement(@Valid @RequestBody ApproveEndorsementRequest request);

    @Operation(summary = "Reject endorsement request")
    @PostMapping("endorsement/reject")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    EndorsementRequestResponse rejectEndorsement(@Valid @RequestBody RejectEndorsementRequest request);

    @Operation(summary = "Cancel policy")
    @PostMapping("cancel")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse cancelPolicy(@Valid @RequestBody CancelPolicyRequest request);

    @Operation(summary = "Request cancellation (9.1) - creates PENDING request for back-office")
    @PostMapping("cancellation/request")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    CancellationRequestResponse requestCancellation(@Valid @RequestBody RequestCancellationRequest request);

    @Operation(summary = "Approve and process cancellation (9.3)")
    @PostMapping("cancellation/approve")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse approveCancellationRequest(@Valid @RequestBody ApproveCancellationRequest request);

    @Operation(summary = "Reject cancellation request")
    @PostMapping("cancellation/reject")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    CancellationRequestResponse rejectCancellationRequest(@Valid @RequestBody RejectCancellationRequest request);

    @Operation(summary = "Cancel policies for non-payment (9.7)")
    @PostMapping("cancellation/non-payment")
    @RolesAllowed({Permissions.IS_ADMIN})
    int cancelPoliciesForNonPayment(@RequestParam(name = "gracePeriodDays", required = false) Integer gracePeriodDays);

    @Operation(summary = "Renew policy - generates renewal quote")
    @PostMapping("renew")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    RenewalResultResponse renewPolicy(@Valid @RequestBody RenewPolicyRequest request);

    @Operation(summary = "Accept renewal quote - customer accepts renewal offer")
    @PostMapping("renewal/accept")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyResponse acceptRenewalQuote(@Valid @RequestBody AcceptRenewalRequest request);

    @Operation(summary = "Suspend policy")
    @PostMapping("suspend")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse suspendPolicy(@Valid @RequestBody SuspendPolicyRequest request);

    @Operation(summary = "Reinstate suspended policy")
    @PostMapping("reinstate")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyResponse reinstatePolicy(@Valid @RequestBody ReinstatePolicyRequest request);

    @Operation(summary = "Get policy by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyResponse getPolicyById(@PathVariable("id") String policyId);

    @Operation(summary = "Get full policy details (12.3) - policy, versions, documents, audit trail")
    @GetMapping("details/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyDetailResponse getPolicyDetails(@PathVariable("id") String policyId);

    @Operation(summary = "Get policy by policy number")
    @GetMapping("get-by-number/{policyNumber}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyResponse getPolicyByNumber(@PathVariable("policyNumber") String policyNumber);

    @Operation(summary = "Get all versions of a policy (12.4)")
    @GetMapping("{policyId}/versions")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    List<PolicyVersionResponse> getPolicyVersions(@PathVariable("policyId") String policyId);

    @Operation(summary = "Get full audit log for policy (14.4) - with sensitive data masking")
    @GetMapping("{policyId}/audit-log")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyAuditLogPageResponse getPolicyAuditLog(
            @PathVariable("policyId") String policyId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size);

    @Operation(summary = "Get compliance violations for policy (14.4)")
    @GetMapping("{policyId}/compliance-violations")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    ComplianceViolationPageResponse getPolicyComplianceViolations(
            @PathVariable("policyId") String policyId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size);

    @Operation(summary = "Get policy audit trail (12.5)")
    @GetMapping("{policyId}/audit-trail")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyAuditTrailPageResponse getPolicyAuditTrail(
            @PathVariable("policyId") String policyId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "List policies with search criteria")
    @PostMapping("search")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    PolicyPageResponse listPolicies(
            @Valid @RequestBody PolicySearchRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "List policies by customer")
    @GetMapping("customer/{customerId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicyPageResponse listPoliciesByCustomer(
            @PathVariable("customerId") String customerId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "Get customer policy summary")
    @GetMapping("summary/customer/{customerId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    PolicySummaryResponse getCustomerPolicySummary(@PathVariable("customerId") String customerId);

    @Operation(summary = "Get renewal reminders")
    @GetMapping("renewal-reminders")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    List<PolicyResponse> getRenewalReminders(@RequestParam(name = "daysAhead", required = false) Integer daysAhead);

    @Operation(summary = "Auto-generate renewal quotes for expiring policies")
    @PostMapping("renewal/generate-quotes")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    int generateRenewalQuotes(@RequestParam(name = "daysAhead", required = false) Integer daysAhead);

    @Operation(summary = "Check if policy can be reinstated")
    @GetMapping("can-reinstate/{policyId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    boolean canReinstate(@PathVariable("policyId") String policyId);

    @Operation(summary = "Get next payment due for policy")
    @GetMapping("next-payment/{policyId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    BillingScheduleResponse getNextPaymentDue(@PathVariable("policyId") String policyId);

    @Operation(summary = "Export policy data (12.7) - CSV, JSON, Excel, PDF")
    @PostMapping("export")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT})
    ResponseEntity<?> exportPolicies(
            @Valid @RequestBody PolicySearchRequest request,
            @RequestParam(name = "format", defaultValue = "json") String format,
            @RequestParam(name = "limit", defaultValue = "10000") int limit);

    // ---- Analytics (Epic 16) ----

    @Operation(summary = "16.1 - Portfolio snapshot")
    @GetMapping("analytics/portfolio-snapshot")
    @RolesAllowed({Permissions.IS_ADMIN})
    PortfolioSnapshotResponse getPortfolioSnapshot();

    @Operation(summary = "16.2 - Policy KPIs")
    @GetMapping("analytics/kpis")
    @RolesAllowed({Permissions.IS_ADMIN})
    PolicyKpisResponse getPolicyKpis();

    @Operation(summary = "16.3 - Renewal rates over time")
    @GetMapping("analytics/renewal-rates")
    @RolesAllowed({Permissions.IS_ADMIN})
    RenewalRatesResponse getRenewalRates(
            @RequestParam(name = "from") LocalDate from,
            @RequestParam(name = "to") LocalDate to,
            @RequestParam(name = "period", defaultValue = "MONTH") String periodType);

    @Operation(summary = "16.4 - Cancellation rates")
    @GetMapping("analytics/cancellation-rates")
    @RolesAllowed({Permissions.IS_ADMIN})
    CancellationRatesResponse getCancellationRates(
            @RequestParam(name = "from") LocalDate from,
            @RequestParam(name = "to") LocalDate to,
            @RequestParam(name = "period", defaultValue = "MONTH") String periodType);

    @Operation(summary = "16.5 - Premium summary by product, branch, or agent")
    @GetMapping("analytics/premium-summary")
    @RolesAllowed({Permissions.IS_ADMIN})
    PremiumSummaryResponse getPremiumSummary(
            @RequestParam(name = "dimension", defaultValue = "PRODUCT") String dimension);

    @Operation(summary = "16.6 - Policy trends over time")
    @GetMapping("analytics/trends")
    @RolesAllowed({Permissions.IS_ADMIN})
    PolicyTrendsResponse getPolicyTrends(
            @RequestParam(name = "from") LocalDate from,
            @RequestParam(name = "to") LocalDate to,
            @RequestParam(name = "period", defaultValue = "MONTH") String periodType);

    @Operation(summary = "16.8 - Publish analytics event to Kafka for BI consumption")
    @PostMapping("analytics/publish")
    @RolesAllowed({Permissions.IS_ADMIN})
    void publishAnalyticsSnapshot();
}
