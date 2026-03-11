package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.*;
import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.analytics.*;
import com.zote.policy.service.domain.models.data.*;
import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import com.zote.policy.service.domain.ports.outbound.*;
import com.zote.policy.service.domain.support.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PolicyImpl implements PolicyPort {

    private final PolicyRepositoryPort policyRepositoryPort;
    private final PolicyVersionRepositoryPort policyVersionRepositoryPort;
    private final PolicyDocumentRepositoryPort policyDocumentRepositoryPort;
    private final PolicyDocumentGeneratorPort policyDocumentGeneratorPort;
    private final BillingScheduleRepositoryPort billingScheduleRepositoryPort;
    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PolicyStatusHistoryRepositoryPort statusHistoryRepositoryPort;
    private final ProductConfigRepositoryPort productConfigRepositoryPort;
    private final QuoteRepositoryPort quoteRepositoryPort;
    private final EndorsementRepositoryPort endorsementRepositoryPort;
    private final EndorsementRequestRepositoryPort endorsementRequestRepositoryPort;
    private final CancellationRequestRepositoryPort cancellationRequestRepositoryPort;

    private final PolicyDefaultsPort policyDefaultsPort;
    private final PolicySupport policySupport;
    private final BillingScheduleGenerator billingScheduleGenerator;
    private final MessagingSupport messagingSupport;
    private final RatingSupport ratingSupport;
    private final QuoteSupport quoteSupport;
    private final PaymentSupport paymentSupport;
    private final EndorsementSupport endorsementSupport;
    private final CancellationSupport cancellationSupport;
    private final RenewalSupport renewalSupport;
    private final PolicyAuditSupport policyAuditSupport;
    private final ProductConfigSupport productConfigSupport;
    private final PolicyAuditLogPort policyAuditLogPort;
    private final ComplianceViolationPort complianceViolationPort;
    private final PolicyAnalyticsPort policyAnalyticsPort;

    @Override
    public Policy createPolicyFromQuote(String quoteId) {
        log.info("Creating policy from accepted quote: {}", quoteId);

        var quote = quoteRepositoryPort.findById(quoteId);
        var productConfig = productConfigRepositoryPort.findByProductId(quote.getProductId());

        policySupport.validatePolicyCreationFromQuote(quote, productConfig);
        int maxRetries = policyDefaultsPort.getPolicyNumberMaxRetries();
        String prefix = policyDefaultsPort.getPolicyNumberPrefix(quote.getPolicyType());
        String policyNumber = policySupport.generatePolicyNumber(quote.getPolicyType(), prefix);
        for (int attempt = 0; policyRepositoryPort.existsByPolicyNumber(policyNumber) && attempt < maxRetries; attempt++) {
            if (attempt == maxRetries - 1) {
                throw new FunctionalError("Failed to generate unique policy number after " + maxRetries + " attempts");
            }
            policyNumber = policySupport.generatePolicyNumber(quote.getPolicyType(), prefix);
        }
        var policyStatus = policySupport.resolveInitialPolicyStatus(quote.getBillingPlan());
        var buildData = BuildPolicyFromQuoteData.builder()
                .policyNumber(policyNumber)
                .policyStatus(policyStatus)
                .quote(quote)
                .productConfigId(productConfig.getId())
                .currency(policyDefaultsPort.getDefaultCurrency())
                .timezone(policyDefaultsPort.getDefaultTimezone())
                .build();
        var policy = PolicyBuilderSupport.buildPolicyFromQuote(buildData);
        productConfigSupport.validateRegulatoryRules(policy, productConfig);
        policy = policyRepositoryPort.savePolicy(policy);

        var version = PolicyBuilderSupport.policyVersionBuilder(policy, quote.getSnapshot());
        policyVersionRepositoryPort.savePolicyVersion(version);

        if (policy.getBillingPlan() != BillingPlan.FULL && productConfig.isAllowInstallments()) {
            var schedules = billingScheduleGenerator.generate(policy, productConfig.getInstallmentsCount());
            billingScheduleRepositoryPort.saveAllSchedules(schedules);
        }

        var policyStatusHistory = PolicyBuilderSupport.policyStatusHistoryBuilder(policy.getId(), null, policy.getStatus(), "Policy created from quote " + quoteId);
        statusHistoryRepositoryPort.saveHistory(policyStatusHistory);
        policyAuditSupport.logChange(policy.getId(), PolicyAuditSupport.CHANGE_CREATE, "Policy created from quote " + quoteId, null, null, null);

        messagingSupport.publishPolicyCreatedEvent(policy);

        log.info("Policy created from quote {} with policyId {}", quoteId, policy.getId());
        return policy;
    }

    @Override
    public Policy issuePolicy(IssuePolicyData data) {
        log.info("Issuing policy {}", data.getPolicyId());

        policySupport.validateIssuePolicyData(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());
        policySupport.validatingAllowedPolicyStatus(policy.getStatus());
        validatePolicyIssuedPreconditions(policy);
        PolicyStatus from = policy.getStatus();
        policyRepositoryPort.updateStatus(data.getPolicyId(), PolicyStatus.ACTIVE);

        statusHistoryRepositoryPort.saveHistory(
                PolicyBuilderSupport.policyStatusHistoryBuilder(
                        data.getPolicyId(),
                        from,
                        PolicyStatus.ACTIVE,
                        "issued"
                )
        );
        policyAuditSupport.logStatusChange(data.getPolicyId(), from.name(), PolicyStatus.ACTIVE.name(), "issued", data.getIssuedBy());

        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setIssuedAt(LocalDateTime.now());
        policyRepositoryPort.savePolicy(policy);

        var scheduleDoc = policyDocumentGeneratorPort.generatePolicyScheduleDocument(policy);
        policyDocumentRepositoryPort.savePolicyDocument(scheduleDoc);

        updatePolicyPaymentStatus(policy);
        messagingSupport.publishPolicyIssuedEvent(policy);

        return policy;
    }

    @Override
    public Payment recordPayment(RecordPaymentData data) {
        log.info("Recording payment for policyId {}", data.getPolicyId());

        paymentSupport.validateRecordPayment(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());

        var payment = PaymentBuilderSupport.buildFromRecordPaymentData(data);
        payment = paymentRepositoryPort.savePayment(payment);

        if (data.getInstallmentNo() != null) {
            var schedule = billingScheduleRepositoryPort.findByPolicyIdAndInstallmentNo(
                    data.getPolicyId(),
                    data.getInstallmentNo()
            );
            schedule.setStatus(BillingStatus.PAID);
            schedule.setPaidAt(java.time.LocalDateTime.now());
            billingScheduleRepositoryPort.saveBillingSchedule(schedule);
        }

        updatePolicyPaymentStatus(policy);
        messagingSupport.publishPaymentRecordedEvent(payment);

        return payment;
    }

    @Override
    public Payment recordFailedPayment(RecordFailedPaymentData data) {
        log.info("Recording failed payment for policyId {}", data.getPolicyId());
        paymentSupport.validateRecordFailedPayment(data);
        var policy = policyRepositoryPort.findById(data.getPolicyId());
        var payment = PaymentBuilderSupport.buildFromRecordFailedPaymentData(data);
        payment = paymentRepositoryPort.savePayment(payment);
        messagingSupport.publishPaymentFailedEvent(payment, policy);
        log.info("Recorded failed payment {} for policy {}", payment.getId(), data.getPolicyId());
        return payment;
    }

    @Override
    public int markBillingSchedulesOverdue() {
        return markBillingSchedulesOverdue(policyDefaultsPort.getAutomationBatchSize());
    }

    /** Batch-limited overdue detection (13.5). */
    public int markBillingSchedulesOverdue(int batchLimit) {
        var today = LocalDate.now();
        var dueSchedules = billingScheduleRepositoryPort.findByStatusAndDueDateBefore(BillingStatus.DUE, today);
        var toProcess = dueSchedules.size() > batchLimit ? dueSchedules.subList(0, batchLimit) : dueSchedules;
        var policyIds = toProcess.stream().map(BillingSchedule::getPolicyId).distinct().toList();
        int marked = 0;
        for (var schedule : toProcess) {
            schedule.setStatus(BillingStatus.OVERDUE);
            billingScheduleRepositoryPort.saveBillingSchedule(schedule);
            marked++;
        }
        for (var policyId : policyIds) {
            var policy = policyRepositoryPort.findById(policyId);
            var schedules = billingScheduleRepositoryPort.findAllByPolicyIdOrderByInstallmentNoAsc(policyId);
            var overdue = schedules.stream().filter(s -> s.getStatus() == BillingStatus.OVERDUE).toList();
            var totalOverdue = overdue.stream().map(BillingSchedule::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            var nextDue = schedules.stream()
                    .filter(s -> s.getStatus() != BillingStatus.PAID && s.getStatus() != BillingStatus.CANCELLED)
                    .map(BillingSchedule::getDueDate)
                    .filter(java.util.Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElse(null);
            messagingSupport.publishPaymentOverdueEvent(policy, overdue.size(), totalOverdue, nextDue);
        }
        if (marked > 0) {
            log.info("Marked {} billing schedules as OVERDUE", marked);
        }
        return marked;
    }

    @Override
    public int runPaymentMonitoring() {
        return markBillingSchedulesOverdue();
    }

    @Override
    public Policy endorsePolicy(EndorsePolicyData data) {
        log.info("Endorsing policy {}", data.getPolicyId());

        endorsementSupport.validateEndorsePolicy(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new FunctionalError("Only active policies can be endorsed");
        }

        var endorsement = applyEndorsement(data);
        return policyRepositoryPort.findById(endorsement.getPolicyId());
    }

    @Override
    public EndorsementRequest requestEndorsement(RequestEndorsementData data) {
        log.info("Requesting endorsement for policy {}", data.getPolicyId());

        endorsementSupport.validateRequestEndorsement(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new FunctionalError("Only active policies can have endorsement requests");
        }

        var request = EndorsementRequest.builder()
                .policyId(data.getPolicyId())
                .type(data.getType() != null ? data.getType() : EndorsementType.OTHER)
                .description(data.getDescription() != null ? data.getDescription() : "Endorsement request")
                .changes(data.getChanges())
                .status(EndorsementRequestStatus.PENDING)
                .requestedBy(data.getRequestedBy())
                .requestedAt(LocalDateTime.now())
                .build();

        request = endorsementRequestRepositoryPort.save(request);
        messagingSupport.publishEndorsementRequestedEvent(request);

        log.info("Endorsement request {} created for policy {}", request.getId(), data.getPolicyId());
        return request;
    }

    @Override
    public Policy approveEndorsement(ApproveEndorsementData data) {
        log.info("Approving endorsement request {}", data.getEndorsementRequestId());

        var request = endorsementRequestRepositoryPort.findById(data.getEndorsementRequestId())
                .orElseThrow(() -> new FunctionalError("Endorsement request not found: " + data.getEndorsementRequestId()));

        if (request.getStatus() != EndorsementRequestStatus.PENDING) {
            throw new FunctionalError("Only PENDING endorsement requests can be approved");
        }

        request.setStatus(EndorsementRequestStatus.APPROVED);
        request.setDecidedBy(data.getDecidedBy());
        request.setDecidedAt(LocalDateTime.now());
        endorsementRequestRepositoryPort.save(request);

        var endorseData = EndorsePolicyData.builder()
                .policyId(request.getPolicyId())
                .type(request.getType())
                .description(request.getDescription())
                .changes(request.getChanges())
                .endorsedBy(data.getDecidedBy())
                .build();

        var endorsement = applyEndorsement(endorseData);
        request.setEndorsementId(endorsement.getId());
        endorsementRequestRepositoryPort.save(request);

        return policyRepositoryPort.findById(endorsement.getPolicyId());
    }

    @Override
    public EndorsementRequest rejectEndorsement(RejectEndorsementData data) {
        log.info("Rejecting endorsement request {}", data.getEndorsementRequestId());

        var request = endorsementRequestRepositoryPort.findById(data.getEndorsementRequestId())
                .orElseThrow(() -> new FunctionalError("Endorsement request not found: " + data.getEndorsementRequestId()));

        if (request.getStatus() != EndorsementRequestStatus.PENDING) {
            throw new FunctionalError("Only PENDING endorsement requests can be rejected");
        }

        request.setStatus(EndorsementRequestStatus.REJECTED);
        request.setDecidedBy(data.getDecidedBy());
        request.setDecidedAt(LocalDateTime.now());
        request.setRejectionReason(data.getRejectionReason());
        request = endorsementRequestRepositoryPort.save(request);

        messagingSupport.publishEndorsementRejectedEvent(request);
        return request;
    }

    /** Applies endorsement changes to policy (7.3, 7.4, 7.5, 7.6). Returns the created Endorsement. */
    private Endorsement applyEndorsement(EndorsePolicyData data) {
        var policy = policyRepositoryPort.findById(data.getPolicyId());

        Integer maxVersionNo = policyVersionRepositoryPort.findMaxVersionNo(policy.getId());
        int nextVersion = (maxVersionNo == null ? 0 : maxVersionNo) + 1;

        var currentVersion = policyVersionRepositoryPort.findCurrentVersion(policy.getId());
        policyVersionRepositoryPort.closeVersion(currentVersion.getId(), LocalDate.now().minusDays(1));

        var updatedSnapshot = PolicyEndorsementSupport.applyChanges(
                currentVersion.getSnapshot(),
                data.getChanges()
        );

        BigDecimal oldPremium = policy.getPremiumTotal();
        BigDecimal newPremium = ratingSupport.recalculatePremiumFromSnapshot(updatedSnapshot, oldPremium);
        BigDecimal premiumChange = newPremium.subtract(oldPremium);

        policy.setPremiumTotal(newPremium);
        policy = policyRepositoryPort.savePolicy(policy);

        var newVersion = PolicyBuilderSupport.policyVersionBuilderForEndorsement(
                policy.getId(), nextVersion, updatedSnapshot, newPremium);
        policyVersionRepositoryPort.savePolicyVersion(newVersion);

        var endorsement = EndorsementBuilderSupport.buildFromEndorseData(data, policy.getId(), premiumChange);
        endorsement = endorsementRepositoryPort.saveEndorsement(endorsement);

        policyAuditSupport.logChange(policy.getId(), PolicyAuditSupport.CHANGE_ENDORSE,
                "Endorsement: premium " + oldPremium + " -> " + newPremium,
                java.util.Map.of("premium", oldPremium),
                java.util.Map.of("premium", newPremium, "endorsementId", endorsement.getId()),
                null);

        messagingSupport.publishPolicyEndorsedEvent(endorsement);
        return endorsement;
    }

    @Override
    public Policy cancelPolicy(CancelPolicyData data) {
        cancellationSupport.validateCancelPolicy(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());
        cancellationSupport.verifyPolicyCanBeCancelled(policy);

        LocalDate effectiveDate = data.getEffectiveDate() != null ? data.getEffectiveDate() : LocalDate.now();
        BigDecimal refundAmount = data.getRefundAmount();
        if (refundAmount == null && policy.getPremiumTotal() != null) {
            refundAmount = cancellationSupport.calculateProRataRefund(policy, effectiveDate);
        }
        var cancelData = CancelPolicyData.builder()
                .policyId(data.getPolicyId())
                .reason(data.getReason())
                .cancelledBy(data.getCancelledBy())
                .cancellationType(data.getCancellationType())
                .effectiveDate(effectiveDate)
                .refundAmount(refundAmount)
                .build();

        PolicyStatus from = policy.getStatus();
        policyRepositoryPort.updateStatus(data.getPolicyId(), PolicyStatus.CANCELLED);

        statusHistoryRepositoryPort.saveHistory(
                PolicyBuilderSupport.policyStatusHistoryBuilder(
                        data.getPolicyId(),
                        from,
                        PolicyStatus.CANCELLED,
                        data.getReason(),
                        data.getCancelledBy()
                    )
        );
        policyAuditSupport.logStatusChange(data.getPolicyId(), from.name(), PolicyStatus.CANCELLED.name(), data.getReason(), data.getCancelledBy());

        policy.setStatus(PolicyStatus.CANCELLED);
        messagingSupport.publishPolicyCancelledEvent(policy, cancelData);
        return policy;
    }

    @Override
    public CancellationRequest requestCancellation(RequestCancellationData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        var policy = policyRepositoryPort.findById(data.getPolicyId());
        cancellationSupport.verifyPolicyCanBeCancelled(policy);

        var request = CancellationRequest.builder()
                .policyId(data.getPolicyId())
                .cancellationType(data.getCancellationType() != null ? data.getCancellationType() : CancellationType.REQUEST)
                .reason(data.getReason() != null ? data.getReason() : "Customer requested cancellation")
                .status(CancellationRequestStatus.PENDING)
                .requestedBy(data.getRequestedBy())
                .requestedAt(LocalDateTime.now())
                .build();
        request = cancellationRequestRepositoryPort.save(request);
        messagingSupport.publishCancellationRequestedEvent(policy, request);
        log.info("Cancellation request {} created for policy {}", request.getId(), data.getPolicyId());
        return request;
    }

    @Override
    public Policy approveCancellationRequest(ApproveCancellationRequestData data) {
        var request = cancellationRequestRepositoryPort.findById(data.getCancellationRequestId())
                .orElseThrow(() -> new FunctionalError("Cancellation request not found: " + data.getCancellationRequestId()));
        if (request.getStatus() != CancellationRequestStatus.PENDING) {
            throw new FunctionalError("Only PENDING cancellation requests can be approved");
        }

        var policy = policyRepositoryPort.findById(request.getPolicyId());
        cancellationSupport.verifyPolicyCanBeCancelled(policy);

        LocalDate effectiveDate = data.getEffectiveDate() != null ? data.getEffectiveDate() : LocalDate.now();
        BigDecimal refundAmount = cancellationSupport.calculateProRataRefund(policy, effectiveDate);

        request.setStatus(CancellationRequestStatus.APPROVED);
        request.setDecidedBy(data.getDecidedBy());
        request.setDecidedAt(LocalDateTime.now());
        request.setEffectiveDate(effectiveDate);
        request.setRefundAmount(refundAmount);
        cancellationRequestRepositoryPort.save(request);

        var cancelData = CancelPolicyData.builder()
                .policyId(request.getPolicyId())
                .reason(request.getReason())
                .cancelledBy(data.getDecidedBy())
                .cancellationType(request.getCancellationType())
                .effectiveDate(effectiveDate)
                .refundAmount(refundAmount)
                .build();
        return cancelPolicy(cancelData);
    }

    @Override
    public CancellationRequest rejectCancellationRequest(RejectCancellationRequestData data) {
        var request = cancellationRequestRepositoryPort.findById(data.getCancellationRequestId())
                .orElseThrow(() -> new FunctionalError("Cancellation request not found: " + data.getCancellationRequestId()));
        if (request.getStatus() != CancellationRequestStatus.PENDING) {
            throw new FunctionalError("Only PENDING cancellation requests can be rejected");
        }
        request.setStatus(CancellationRequestStatus.REJECTED);
        request.setDecidedBy(data.getDecidedBy());
        request.setDecidedAt(LocalDateTime.now());
        request.setRejectionReason(data.getRejectionReason());
        return cancellationRequestRepositoryPort.save(request);
    }

    @Override
    public int cancelPoliciesForNonPayment(int gracePeriodDays) {
        return cancelPoliciesForNonPayment(gracePeriodDays, policyDefaultsPort.getAutomationBatchSize());
    }

    /** Batch-limited non-payment cancellation (13.5). */
    public int cancelPoliciesForNonPayment(int gracePeriodDays, int batchLimit) {
        LocalDate threshold = LocalDate.now().minusDays(gracePeriodDays);
        var allPolicyIds = billingScheduleRepositoryPort.findDistinctPolicyIdsWithOverdueBilling(threshold);
        var policyIds = allPolicyIds.size() > batchLimit ? allPolicyIds.subList(0, batchLimit) : allPolicyIds;
        int cancelled = 0;
        for (var policyId : policyIds) {
            try {
                var policy = policyRepositoryPort.findById(policyId);
                if (policy.getStatus() != PolicyStatus.ACTIVE) {
                    continue;
                }
                int effectiveGrace = policy.getGracePeriodDays() != null ? policy.getGracePeriodDays() : gracePeriodDays;
                if (effectiveGrace > gracePeriodDays) {
                    continue;
                }
                LocalDate policyThreshold = LocalDate.now().minusDays(effectiveGrace);
                var schedules = billingScheduleRepositoryPort.findAllByPolicyIdOrderByInstallmentNoAsc(policyId);
                boolean overduePastGrace = schedules.stream()
                        .filter(s -> s.getStatus() == BillingStatus.DUE || s.getStatus() == BillingStatus.OVERDUE)
                        .anyMatch(s -> s.getDueDate() != null && !s.getDueDate().isAfter(policyThreshold));
                if (!overduePastGrace) {
                    continue;
                }
                cancelPolicy(CancelPolicyData.builder()
                        .policyId(policyId)
                        .reason("Non-payment: overdue by more than " + gracePeriodDays + " days")
                        .cancelledBy("SYSTEM")
                        .cancellationType(CancellationType.NON_PAYMENT)
                        .build());
                cancelled++;
            } catch (Exception e) {
                log.warn("Failed to cancel policy {} for non-payment: {}", policyId, e.getMessage());
            }
        }
        log.info("Cancelled {} policies for non-payment (grace period {} days)", cancelled, gracePeriodDays);
        return cancelled;
    }

    @Override
    public RenewalResult renewPolicy(RenewPolicyData data) {
        renewalSupport.validateRenewPolicy(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());
        renewalSupport.verifyPolicyCanBeRenewed(policy);

        var existingRenewalQuotes = quoteRepositoryPort.findByParentPolicyIdAndStatus(policy.getId(), QuoteStatus.APPROVED);
        if (!existingRenewalQuotes.isEmpty()) {
            var existing = existingRenewalQuotes.get(0);
            if (existing.getValidUntil() != null && existing.getValidUntil().isAfter(java.time.LocalDateTime.now())) {
                return RenewalResult.builder()
                        .policyId(policy.getId())
                        .renewalQuoteId(existing.getId())
                        .renewalPremium(existing.getPremiumTotal())
                        .newExpiryDate(existing.getExpiryDate())
                        .build();
            }
        }

        var currentVersion = policyVersionRepositoryPort.findCurrentVersion(policy.getId());
        renewalSupport.verifyCurrentVersionExists(currentVersion);

        var productConfig = productConfigRepositoryPort.findById(policy.getProductConfigId());

        LocalDate newEffectiveDate = policy.getExpiryDate().plusDays(1);
        LocalDate newExpiryDate = switch (productConfig.getTermUnit()) {
            case MONTHS -> newEffectiveDate.plusMonths(productConfig.getTermLength()).minusDays(1);
            case YEARS -> newEffectiveDate.plusYears(productConfig.getTermLength()).minusDays(1);
            case TRIP_DATES -> throw new FunctionalError("Travel policies are not renewable by standard renewal flow");
        };

        BigDecimal renewalPremium = ratingSupport.recalculatePremiumFromSnapshot(
                currentVersion.getSnapshot(),
                policy.getPremiumTotal()
        );

        BigDecimal taxRate = policyDefaultsPort.getDefaultTaxRate();
        int validityDays = policyDefaultsPort.getRenewalQuoteValidityDays();
        BigDecimal premiumBeforeTax = renewalPremium.divide(taxRate, 2, java.math.RoundingMode.HALF_UP);
        BigDecimal taxAmount = renewalPremium.subtract(premiumBeforeTax);

        var renewalQuoteData = RenewalQuoteBuildData.builder()
                .policy(policy)
                .currentVersion(currentVersion)
                .newEffectiveDate(newEffectiveDate)
                .newExpiryDate(newExpiryDate)
                .renewalPremium(renewalPremium)
                .premiumBeforeTax(premiumBeforeTax)
                .taxAmount(taxAmount)
                .validityDays(validityDays)
                .renewalQuoteNumberPrefix(policyDefaultsPort.getRenewalQuoteNumberPrefix())
                .snapshot(currentVersion.getSnapshot())
                .build();
        Quote renewalQuote = QuoteBuilderSupport.buildRenewalQuote(renewalQuoteData);
        renewalQuote = quoteRepositoryPort.saveQuote(renewalQuote);

        messagingSupport.publishRenewalQuoteCreatedEvent(policy, renewalQuote);

        return RenewalResult.builder()
                .policyId(policy.getId())
                .renewalQuoteId(renewalQuote.getId())
                .renewalPremium(renewalQuote.getPremiumTotal())
                .newExpiryDate(newExpiryDate)
                .build();
    }

    @Override
    public Policy acceptRenewalQuote(AcceptRenewalData data) {
        log.info("Accepting renewal quote {}", data.getRenewalQuoteId());

        var quote = quoteRepositoryPort.findById(data.getRenewalQuoteId());
        renewalSupport.verifyRenewalQuoteEligibility(quote);

        var policy = policyRepositoryPort.findById(quote.getParentPolicyId());
        renewalSupport.verifyPolicyCanBeRenewed(policy);
        PolicyStatus fromStatus = policy.getStatus();

        var currentVersion = policyVersionRepositoryPort.findCurrentVersion(policy.getId());
        renewalSupport.verifyCurrentVersionExists(currentVersion);

        policyVersionRepositoryPort.closeVersion(currentVersion.getId(), policy.getExpiryDate());

        Integer maxVersionNo = policyVersionRepositoryPort.findMaxVersionNo(policy.getId());
        int nextVersion = (maxVersionNo == null ? 0 : maxVersionNo) + 1;

        var newVersion = PolicyBuilderSupport.policyVersionBuilderForRenewal(
                policy.getId(),
                nextVersion,
                quote.getSnapshot(),
                quote.getPremiumTotal(),
                quote.getEffectiveDate(),
                quote.getExpiryDate()
        );
        policyVersionRepositoryPort.savePolicyVersion(newVersion);

        policy.setEffectiveDate(quote.getEffectiveDate());
        policy.setExpiryDate(quote.getExpiryDate());
        policy.setPremiumTotal(quote.getPremiumTotal());
        if (policy.getStatus() == PolicyStatus.EXPIRED) {
            policy.setStatus(PolicyStatus.ACTIVE);
        }
        policy = policyRepositoryPort.savePolicy(policy);

        statusHistoryRepositoryPort.saveHistory(
                PolicyBuilderSupport.policyStatusHistoryBuilder(
                        policy.getId(),
                        fromStatus,
                        policy.getStatus(),
                        "Renewed via quote " + quote.getQuoteNumber(),
                        data.getAcceptedBy()
                )
        );
        policyAuditSupport.logStatusChange(policy.getId(), fromStatus.name(), policy.getStatus().name(), "Renewed via quote " + quote.getQuoteNumber(), data.getAcceptedBy());

        quote.setStatus(QuoteStatus.ACCEPTED);
        quoteRepositoryPort.saveQuote(quote);

        messagingSupport.publishPolicyRenewedEvent(policy, quote);
        messagingSupport.publishQuoteAcceptedEvent(quote);

        log.info("Renewal accepted for policy {} - new term {} to {}", policy.getPolicyNumber(), quote.getEffectiveDate(), quote.getExpiryDate());
        return policyRepositoryPort.findById(policy.getId());
    }

    @Override
    public Policy suspendPolicy(SuspendPolicyData data) {
        policySupport.validateSuspendPolicyData(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new FunctionalError("Only active policies can be suspended");
        }

        PolicyStatus from = policy.getStatus();
        policyRepositoryPort.updateStatus(data.getPolicyId(), PolicyStatus.SUSPENDED);

        statusHistoryRepositoryPort.saveHistory(
                PolicyBuilderSupport.policyStatusHistoryBuilder(
                        data.getPolicyId(),
                        from,
                        PolicyStatus.SUSPENDED,
                        data.getReason()
                )
        );
        policyAuditSupport.logStatusChange(data.getPolicyId(), from.name(), PolicyStatus.SUSPENDED.name(), data.getReason(), null);

        policy.setStatus(PolicyStatus.SUSPENDED);
        return policy;
    }

    @Override
    public Policy reinstatePolicy(ReinstatePolicyData data) {
        policySupport.validateReinstatePolicyData(data);

        var policy = policyRepositoryPort.findById(data.getPolicyId());

        if (policy.getStatus() != PolicyStatus.SUSPENDED) {
            throw new FunctionalError("Only suspended policies can be reinstated");
        }
        if (!policySupport.canReinstatePolicy(policy)) {
            throw new FunctionalError("Cannot reinstate: policy is expired or not eligible for reinstatement");
        }

        PolicyStatus from = policy.getStatus();
        policyRepositoryPort.updateStatus(data.getPolicyId(), PolicyStatus.ACTIVE);

        statusHistoryRepositoryPort.saveHistory(
                PolicyBuilderSupport.policyStatusHistoryBuilder(
                        data.getPolicyId(),
                        from,
                        PolicyStatus.ACTIVE,
                        data.getReason()
                )
        );
        policyAuditSupport.logStatusChange(data.getPolicyId(), from.name(), PolicyStatus.ACTIVE.name(), data.getReason(), null);

        policy.setStatus(PolicyStatus.ACTIVE);
        return policy;
    }

    @Override
    @Transactional(readOnly = true)
    public Policy findPolicyById(String policyId) {
        return policyRepositoryPort.findById(policyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Policy findByPolicyNumber(String policyNumber) {
        return policyRepositoryPort.findByPolicyNumber(policyNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public Policy findPolicyDetailsById(String policyId) {
        return policyRepositoryPort.findById(policyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyVersion> getPolicyVersions(String policyId) {
        policyRepositoryPort.findById(policyId); // ensure policy exists
        return policyVersionRepositoryPort.findAllByPolicyIdOrderByVersionNoDesc(policyId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyStatusHistory> getPolicyAuditTrail(String policyId, PageParams pageParams) {
        policyRepositoryPort.findById(policyId); // ensure policy exists
        return statusHistoryRepositoryPort.findAllByPolicyId(policyId, pageParams.toPageable());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Policy> listPolicies(PolicySearchCriteria criteria, PageParams pageParams) {
        Pageable pageable = pageParams.toPageable();

        // Use combined search when any criteria are set (12.1, 12.2)
        if (criteria.hasCriteria()) {
            return policyRepositoryPort.searchPolicies(criteria, pageable);
        }

        return policyRepositoryPort.findAllByStatus(PolicyStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Policy> listPoliciesByCustomer(String customerId, PageParams pageParams) {
        return policyRepositoryPort.findAllByCustomerId(customerId, pageParams.toPageable());
    }

    @Override
    @Transactional(readOnly = true)
    public PolicySummary getCustomerPolicySummary(String customerId) {
        var pageable = PageRequest.of(0, Integer.MAX_VALUE);
        var policies = policyRepositoryPort.findAllByCustomerId(customerId, pageable).getContent();

        long active = policies.stream().filter(p -> p.getStatus() == PolicyStatus.ACTIVE).count();
        long expired = policies.stream().filter(p -> p.getStatus() == PolicyStatus.EXPIRED).count();
        long cancelled = policies.stream().filter(p -> p.getStatus() == PolicyStatus.CANCELLED).count();

        return PolicySummary.builder()
                .customerId(customerId)
                .totalPolicies((long) policies.size())
                .activePolicies(active)
                .expiredPolicies(expired)
                .cancelledPolicies(cancelled)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Policy> getRenewalReminders(int daysAhead) {
        var from = LocalDate.now();
        var to = LocalDate.now().plusDays(daysAhead);
        return policyRepositoryPort.findExpiringBetween(from, to, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public int generateRenewalQuotesForExpiringPolicies(int daysAhead) {
        return generateRenewalQuotesForExpiringPolicies(daysAhead, policyDefaultsPort.getAutomationBatchSize());
    }

    /** Batch-limited renewal quote generation (13.5). */
    public int generateRenewalQuotesForExpiringPolicies(int daysAhead, int batchLimit) {
        var from = LocalDate.now();
        var to = LocalDate.now().plusDays(daysAhead);
        var policies = policyRepositoryPort.findExpiringBetween(from, to, PageRequest.of(0, batchLimit)).getContent();
        int generated = 0;
        for (var policy : policies) {
            try {
                var existing = quoteRepositoryPort.findByParentPolicyIdAndStatus(policy.getId(), QuoteStatus.APPROVED);
                if (existing.stream().anyMatch(q -> q.getValidUntil() != null && q.getValidUntil().isAfter(LocalDateTime.now()))) {
                    continue;
                }
                renewPolicy(RenewPolicyData.builder().policyId(policy.getId()).build());
                generated++;
            } catch (Exception e) {
                log.warn("Failed to generate renewal quote for policy {}: {}", policy.getPolicyNumber(), e.getMessage());
            }
        }
        log.info("Generated {} renewal quotes for expiring policies ({} days ahead)", generated, daysAhead);
        return generated;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canReinstate(String policyId) {
        var policy = policyRepositoryPort.findById(policyId);
        return policySupport.canReinstatePolicy(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BillingSchedule> getNextPaymentDue(String policyId) {
        var schedules = billingScheduleRepositoryPort.findAllByPolicyIdOrderByInstallmentNoAsc(policyId);
        return schedules.stream()
                .filter(s -> s.getStatus() != BillingStatus.PAID && s.getStatus() != BillingStatus.CANCELLED)
                .findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyAuditLog> getPolicyAuditLog(String policyId, PageParams pageParams) {
        policyRepositoryPort.findById(policyId); // ensure exists
        return policyAuditLogPort.findByPolicyId(policyId, pageParams.toPageable());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceViolation> getComplianceViolations(String policyId, PageParams pageParams) {
        policyRepositoryPort.findById(policyId); // ensure exists
        return complianceViolationPort.findByPolicyId(policyId, pageParams.toPageable());
    }

    @Override
    public PortfolioSnapshot getPortfolioSnapshot() {
        return policyAnalyticsPort.getPortfolioSnapshot();
    }

    @Override
    public PolicyKpis getPolicyKpis() {
        return policyAnalyticsPort.getPolicyKpis();
    }

    @Override
    public RenewalRatesSummary getRenewalRates(LocalDate from, LocalDate to, String periodType) {
        return policyAnalyticsPort.getRenewalRates(from, to, periodType);
    }

    @Override
    public CancellationRatesSummary getCancellationRates(LocalDate from, LocalDate to, String periodType) {
        return policyAnalyticsPort.getCancellationRates(from, to, periodType);
    }

    @Override
    public PremiumSummary getPremiumSummary(String dimension) {
        return policyAnalyticsPort.getPremiumSummary(dimension);
    }

    @Override
    public PolicyTrends getPolicyTrends(LocalDate from, LocalDate to, String periodType) {
        return policyAnalyticsPort.getPolicyTrends(from, to, periodType);
    }

    @Override
    public void publishAnalyticsSnapshot() {
        var snapshot = policyAnalyticsPort.getPortfolioSnapshot();
        messagingSupport.publishAnalyticsPortfolioSnapshotEvent(snapshot);
    }

    @Override
    public Optional<Policy> tryAutoIssuePolicyIfEligible(String policyId) {
        try {
            var policy = policyRepositoryPort.findById(policyId);
            policySupport.validatingAllowedPolicyStatus(policy.getStatus());
            validatePolicyIssuedPreconditions(policy);
            var issued = issuePolicy(IssuePolicyData.builder().policyId(policyId).issuedBy("SYSTEM").build());
            log.info("Auto-issued policy {} on event (13.4)", policyId);
            return Optional.of(issued);
        } catch (FunctionalError e) {
            log.debug("Policy {} not eligible for auto-issue: {}", policyId, e.getMessage());
            return Optional.empty();
        }
    }

    private void validatePolicyIssuedPreconditions(Policy policy) {
        var productConfig = productConfigRepositoryPort.findById(
                productConfigRepositoryPort.findByProductId(policy.getProductId()).getId()
        );

        var requiredDocuments = productConfigRepositoryPort
                .findMandatoryRequiredDocuments(productConfig.getId())
                .stream()
                .map(policyRequiredDocument -> PolicyDocumentType.valueOf(policyRequiredDocument.getDocumentType().name()))
                .toList();

        boolean docsOk = policyDocumentRepositoryPort.hasAllRequiredDocumentsVerified(policy.getId(), requiredDocuments);

        if (!docsOk) {
            throw new FunctionalError("Cannot issue policy: not all required documents are verified");
        }

        switch (policy.getBillingPlan()) {
            case FULL -> {
                var totalPaid = paymentRepositoryPort.getTotalPaidAmount(policy.getId());
                if (totalPaid.compareTo(policy.getPremiumTotal()) < 0) {
                    throw new FunctionalError("Cannot issue policy: full payment not completed");
                }
            }
            case MONTHLY, QUARTERLY -> {
                boolean firstInstallmentPaid = paymentRepositoryPort.hasPaidInstallment(policy.getId(), 1);
                if (!firstInstallmentPaid) {
                    throw new FunctionalError("Cannot issue policy: first installment not paid");
                }
            }
            default -> throw new FunctionalError("Unsupported billing plan: " + policy.getBillingPlan());
        }
    }

    private void updatePolicyPaymentStatus(Policy policy) {
        BigDecimal totalPaid = paymentRepositoryPort.getTotalPaidAmount(policy.getId());

        if (totalPaid.compareTo(BigDecimal.ZERO) <= 0) {
            policy.setPaymentStatus(PaymentStatus.UNPAID);
        } else if (totalPaid.compareTo(policy.getPremiumTotal()) < 0) {
            policy.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        } else {
            policy.setPaymentStatus(PaymentStatus.PAID);
        }

        policyRepositoryPort.savePolicy(policy);
    }
}