package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.BillingSchedule;
import com.zote.policy.service.domain.models.Policy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class BillingScheduleGenerator {

    /**
     * Generates billing schedule with due dates aligned to billing plan:
     * MONTHLY: each installment due every 1 month
     * QUARTERLY: each installment due every 3 months
     */
    public List<BillingSchedule> generate(Policy policy, int installmentsCount) {
        if (policy.getBillingPlan() == BillingPlan.FULL) return List.of();

        if (installmentsCount <= 0)
            throw new FunctionalError("installmentsCount must be > 0 for installment plans");

        int monthsPerInstallment = switch (policy.getBillingPlan()) {
            case MONTHLY -> 1;
            case QUARTERLY -> 3;
            default -> 1;
        };

        var installmentAmount = policy.getPremiumTotal()
                .divide(BigDecimal.valueOf(installmentsCount), 2, RoundingMode.HALF_UP);

        return IntStream.rangeClosed(1, installmentsCount)
                .mapToObj(i -> {
                    var dueDate = policy.getEffectiveDate().plusMonths((i - 1) * monthsPerInstallment);
                    return BillingScheduleBuilderSupport.buildInstallment(
                            policy.getId(), i, dueDate, installmentAmount);
                })
                .toList();
    }
}
