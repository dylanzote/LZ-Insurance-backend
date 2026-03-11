package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyVersion;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.data.RenewPolicyData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RenewalSupport {

    /**
     * Validates renew policy data.
     * @throws FunctionalError if data is invalid
     */
    public void validateRenewPolicy(RenewPolicyData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
    }

    /**
     * Verifies that the policy can be renewed based on status and eligibility (8.5).
     * @throws FunctionalError if policy cannot be renewed
     */
    public void verifyPolicyCanBeRenewed(Policy policy) {
        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new FunctionalError("Cancelled policies cannot be renewed");
        }
        if (policy.getStatus() != PolicyStatus.ACTIVE && policy.getStatus() != PolicyStatus.EXPIRED) {
            throw new FunctionalError("Only active or expired policies can be renewed");
        }
    }

    /**
     * Verifies that a current version exists for renewal.
     * @throws FunctionalError if no current version
     */
    public void verifyCurrentVersionExists(PolicyVersion currentVersion) {
        if (currentVersion == null) {
            throw new FunctionalError("No current policy version found for renewal");
        }
    }

    /**
     * Verifies that a quote is a valid renewal quote that can be accepted (8.5).
     * @throws FunctionalError if quote cannot be accepted for renewal
     */
    public void verifyRenewalQuoteEligibility(Quote quote) {
        if (quote.getParentPolicyId() == null || quote.getParentPolicyId().isBlank()) {
            throw new FunctionalError("Quote is not a renewal quote (missing parent policy)");
        }
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw new FunctionalError("Renewal quote must be approved to accept");
        }
        if (quote.getValidUntil() != null && quote.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new FunctionalError("Renewal quote has expired");
        }
    }
}
