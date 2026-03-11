package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingSchedule;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Centralized builders for BillingSchedule.
 * Keeps construction logic consistent with project rules.
 */
@UtilityClass
public class BillingScheduleBuilderSupport {

    /**
     * Builds a single billing schedule installment.
     */
    public BillingSchedule buildInstallment(String policyId, int installmentNo, LocalDate dueDate, BigDecimal amount) {
        return BillingSchedule.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .installmentNo(installmentNo)
                .dueDate(dueDate)
                .amount(amount)
                .status(BillingStatus.DUE)
                .build();
    }
}
