package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPaymentData {
    private String policyId;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDate paymentDate;
    private String transactionId;
    private Integer installmentNo;
    private String recordedBy;
}
