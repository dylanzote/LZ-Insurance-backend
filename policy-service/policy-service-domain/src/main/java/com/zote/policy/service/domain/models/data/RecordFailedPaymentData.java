package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data for recording a failed payment attempt (10.4).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordFailedPaymentData {
    private String policyId;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDate attemptedDate;
    private String failureReason;
    private String transactionId;
    private Integer installmentNo;
    private String recordedBy;
}
