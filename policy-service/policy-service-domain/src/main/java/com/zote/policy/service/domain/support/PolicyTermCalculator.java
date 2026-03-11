package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PolicyTermCalculator {

    public LocalDate calculateExpiryDate(CreateQuoteData data, PolicyProductConfig cfg) {
        LocalDate start = data.getEffectiveDate();

        return switch (cfg.getTermUnit()) {
            case MONTHS -> start.plusMonths(cfg.getTermLength()).minusDays(1);
            case YEARS -> start.plusYears(cfg.getTermLength()).minusDays(1);
            case TRIP_DATES -> calculateTravelExpiryDate(data.getRatingData());
        };
    }

    private LocalDate calculateTravelExpiryDate(Map<String, Object> ratingData) {
        if (ratingData == null || ratingData.isEmpty()) {
            throw new FunctionalError("ratingData is required for travel policies");
        }

        Object tripStart = ratingData.get("tripStartDate");
        Object tripEnd = ratingData.get("tripEndDate");

        if (tripStart == null || tripEnd == null) {
            throw new FunctionalError("tripStartDate and tripEndDate are required for travel policies");
        }

        LocalDate tripStartDate = parseDate(tripStart, "tripStartDate");
        LocalDate tripEndDate = parseDate(tripEnd, "tripEndDate");

        if (!tripEndDate.isAfter(tripStartDate)) {
            throw new FunctionalError("tripEndDate must be after tripStartDate");
        }

        return tripEndDate;
    }

    private LocalDate parseDate(Object value, String fieldName) {
        try {
            if (value instanceof LocalDate localDate) {
                return localDate;
            }
            return LocalDate.parse(value.toString());
        } catch (Exception e) {
            throw new FunctionalError("Invalid date format for " + fieldName + ": " + value);
        }
    }
}
