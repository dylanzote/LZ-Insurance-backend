package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.data.RecordFailedPaymentData;
import com.zote.policy.service.domain.models.data.RecordPaymentData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentSupport {

    /**
     * Validates record payment data.
     * @throws FunctionalError if data is invalid
     */
    public void validateRecordPayment(RecordPaymentData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (data.getAmount() == null || data.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new FunctionalError("amount must be greater than zero");
        }
        if (data.getMethod() == null) {
            throw new FunctionalError("payment method is required");
        }
        if (data.getPaymentDate() == null) {
            throw new FunctionalError("paymentDate is required");
        }
    }

    /**
     * Validates record failed payment data (10.4).
     */
    public void validateRecordFailedPayment(RecordFailedPaymentData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (data.getMethod() == null) {
            throw new FunctionalError("payment method is required");
        }
        if (data.getFailureReason() == null || data.getFailureReason().isBlank()) {
            throw new FunctionalError("failureReason is required for failed payment");
        }
    }
}
