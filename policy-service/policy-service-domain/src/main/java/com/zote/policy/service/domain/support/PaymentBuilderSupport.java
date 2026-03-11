package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.domain.models.Payment;
import com.zote.policy.service.domain.models.data.RecordFailedPaymentData;
import com.zote.policy.service.domain.models.data.RecordPaymentData;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Builder support for Payment domain entity.
 * Centralizes construction logic per project rules.
 */
@UtilityClass
public class PaymentBuilderSupport {

    public Payment buildFromRecordPaymentData(RecordPaymentData data) {
        return Payment.builder()
                .id(UUID.randomUUID().toString())
                .policyId(data.getPolicyId())
                .amount(data.getAmount())
                .method(data.getMethod())
                .paymentDate(data.getPaymentDate())
                .transactionId(data.getTransactionId())
                .status(PaymentRecordStatus.RECORDED)
                .installmentNo(data.getInstallmentNo())
                .createdBy(data.getRecordedBy())
                .build();
    }

    public Payment buildFromRecordFailedPaymentData(RecordFailedPaymentData data) {
        return Payment.builder()
                .id(UUID.randomUUID().toString())
                .policyId(data.getPolicyId())
                .amount(data.getAmount() != null ? data.getAmount() : java.math.BigDecimal.ZERO)
                .method(data.getMethod())
                .paymentDate(data.getAttemptedDate() != null ? data.getAttemptedDate() : java.time.LocalDate.now())
                .transactionId(data.getTransactionId())
                .status(PaymentRecordStatus.FAILED)
                .installmentNo(data.getInstallmentNo())
                .failureReason(data.getFailureReason())
                .createdBy(data.getRecordedBy())
                .build();
    }
}
