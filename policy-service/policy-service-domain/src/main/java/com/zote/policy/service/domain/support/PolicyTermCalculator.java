package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.TravelPolicySnapshot;
import com.zote.policy.service.domain.models.data.CreatePolicyData;
import com.zote.policy.service.domain.ports.outbound.ProductConfigRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PolicyTermCalculator {

    private final ProductConfigRepositoryPort productConfigRepositoryPort;

    public LocalDate calculateExpiryDate(CreatePolicyData data, PolicyProductConfig cfg) {
        LocalDate start = data.getEffectiveDate();

        return switch (cfg.getTermUnit()) {
            case MONTHS -> start.plusMonths(cfg.getTermLength()).minusDays(1);
            case YEARS  -> start.plusYears(cfg.getTermLength()).minusDays(1);
            case TRIP_DATES -> {
                if (!(data.getSnapshot() instanceof TravelPolicySnapshot travel))
                    throw new FunctionalError("TRAVEL snapshot is required for travel products");
                if (travel.getTripStartDate() == null || travel.getTripEndDate() == null)
                    throw new FunctionalError("tripStartDate and tripEndDate are required for travel policies");
                if (!travel.getTripEndDate().isAfter(travel.getTripStartDate()))
                    throw new FunctionalError("tripEndDate must be after tripStartDate");
                yield travel.getTripEndDate();
            }
        };
    }
}
