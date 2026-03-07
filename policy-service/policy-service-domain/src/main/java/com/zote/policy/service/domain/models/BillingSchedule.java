package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.BillingStatus;
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
public class BillingSchedule {
    private String id;
    private String policyId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal amount;
    private BillingStatus status;
    private LocalDateTime paidAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
