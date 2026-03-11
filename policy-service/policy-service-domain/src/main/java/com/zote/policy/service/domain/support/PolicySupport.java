package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.data.IssuePolicyData;
import com.zote.policy.service.domain.models.data.ReinstatePolicyData;
import com.zote.policy.service.domain.models.data.SuspendPolicyData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicySupport {

    private final QuoteSupport quoteSupport;
    private final ProductConfigSupport productConfigSupport;

    /**
     * Validates policy creation from quote: quote readiness, dates, product rules.
     */
    public void validatePolicyCreationFromQuote(Quote quote, PolicyProductConfig productConfig) {
        quoteSupport.verifyQuote(quote);
        validateEffectiveExpiryDates(quote.getEffectiveDate(), quote.getExpiryDate());
        productConfigSupport.validateBillingPlanAgainstConfig(quote, productConfig);
    }

    /**
     * Validates issue policy data.
     */
    public void validateIssuePolicyData(IssuePolicyData data) {
        if (data == null || data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
    }

    /**
     * Validates suspend policy data.
     */
    public void validateSuspendPolicyData(SuspendPolicyData data) {
        if (data == null || data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
    }

    /**
     * Validates reinstate policy data.
     */
    public void validateReinstatePolicyData(ReinstatePolicyData data) {
        if (data == null || data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
    }

    /**
     * Validates effectiveDate <= expiryDate.
     * @throws FunctionalError if dates are invalid
     */
    public void validateEffectiveExpiryDates(LocalDate effectiveDate, LocalDate expiryDate) {
        if (effectiveDate != null && expiryDate != null && effectiveDate.isAfter(expiryDate)) {
            throw new FunctionalError("effectiveDate must be before or equal to expiryDate");
        }
    }

    /**
     * Validates that a policy can be reinstated (status SUSPENDED and not expired).
     */
    public boolean canReinstatePolicy(Policy policy) {
        if (policy.getStatus() != PolicyStatus.SUSPENDED) {
            return false;
        }
        if (policy.getExpiryDate() != null && policy.getExpiryDate().isBefore(LocalDate.now())) {
            return false;
        }
        return true;
    }

    public void validatingAllowedPolicyStatus(PolicyStatus status) {
        if (!(status == PolicyStatus.PENDING
                || status == PolicyStatus.PENDING_DOCUMENTS
                || status == PolicyStatus.PENDING_PAYMENT
                || status == PolicyStatus.UNDER_REVIEW
                || status == PolicyStatus.DRAFT)) {
            throw new FunctionalError("Policy cannot be issued from status: " + status);
        }
    }

    public PolicyStatus resolveInitialPolicyStatus(BillingPlan billingPlan) {
        return switch (billingPlan) {
            case FULL, MONTHLY, QUARTERLY -> PolicyStatus.PENDING_PAYMENT;
        };
    }

    /**
     * Generates a unique-style policy number with the given prefix.
     * Prefix should come from config (e.g. PolicyDefaultsConfig.getPolicyNumberPrefix).
     */
    public String generatePolicyNumber(PolicyType type, String prefix) {
        String p = (prefix != null && !prefix.isBlank()) ? prefix : "POL";
        return p + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

}
