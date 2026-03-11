package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PaymentMethod;
import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Payment {
    private String id;
    private String policyId;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDate paymentDate;
    private String transactionId;
    private PaymentRecordStatus status;
    private Integer installmentNo;
    /** Reason for failure when status is FAILED (10.4). */
    private String failureReason;
    private LocalDateTime recordedAt; // maps Auditable.createdAt
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
