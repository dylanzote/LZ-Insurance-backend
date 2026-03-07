package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.BillingSchedule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
public class BillingScheduleGenerator {
    public List<BillingSchedule> generate(String policyId,
                                          BillingPlan plan,
                                          BigDecimal premiumTotal,
                                          LocalDate effectiveDate,
                                          int installmentsCount) {
        if (plan == BillingPlan.FULL) return List.of();

        if (installmentsCount <= 0)
            throw new FunctionalError("installmentsCount must be > 0 for installment plans");

        BigDecimal installmentAmount = premiumTotal
                .divide(BigDecimal.valueOf(installmentsCount), 2, java.math.RoundingMode.HALF_UP);

        return java.util.stream.IntStream.rangeClosed(1, installmentsCount)
                .mapToObj(i -> BillingSchedule.builder()
                        .id(UUID.randomUUID().toString())
                        .policyId(policyId)
                        .installmentNo(i)
                        .dueDate(effectiveDate.plusMonths(i - 1)) // first due on effective date
                        .amount(installmentAmount)
                        .status(BillingStatus.DUE)
                        .build())
                .toList();
    }
}
