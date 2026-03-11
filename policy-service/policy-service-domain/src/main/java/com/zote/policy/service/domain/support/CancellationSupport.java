package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.data.CancelPolicyData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class CancellationSupport {

    /**
     * Validates cancel policy data.
     * @throws FunctionalError if data is invalid
     */
    public void validateCancelPolicy(CancelPolicyData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
    }

    /**
     * Verifies that the policy can be cancelled based on its current status.
     * @throws FunctionalError if policy cannot be cancelled
     */
    public void verifyPolicyCanBeCancelled(Policy policy) {
        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new FunctionalError("Policy is already cancelled");
        }
        if (policy.getStatus() == PolicyStatus.RENEWED) {
            throw new FunctionalError("Policy has been renewed; use the renewal flow for changes");
        }
    }

    /**
     * Calculates pro-rata refund for policy cancellation (9.2).
     * Refund = premium × (days remaining from effective date to expiry) / total policy days.
     * @param policy the policy being cancelled
     * @param effectiveDate date when cancellation takes effect
     * @return refund amount, or BigDecimal.ZERO if no refund applies
     */
    public BigDecimal calculateProRataRefund(Policy policy, LocalDate effectiveDate) {
        if (policy.getPremiumTotal() == null || policy.getPremiumTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        LocalDate start = policy.getEffectiveDate();
        LocalDate end = policy.getExpiryDate();
        if (start == null || end == null || effectiveDate == null) {
            return BigDecimal.ZERO;
        }
        if (!effectiveDate.isBefore(end) && !effectiveDate.isEqual(end)) {
            return BigDecimal.ZERO;
        }
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        if (totalDays <= 0) {
            return BigDecimal.ZERO;
        }
        long daysRemaining = ChronoUnit.DAYS.between(effectiveDate, end) + 1;
        if (daysRemaining <= 0) {
            return BigDecimal.ZERO;
        }
        return policy.getPremiumTotal()
                .multiply(BigDecimal.valueOf(daysRemaining))
                .divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);
    }
}
