package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.RenewalRatePeriod;
import com.zote.policy.service.domain.models.analytics.RenewalRatesSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewalRatesResponse {
    private List<RenewalRatePeriodResponse> periods;
    private Double overallRenewalRate;
    private LocalDateTime asOf;

    public static RenewalRatesResponse from(RenewalRatesSummary summary) {
        List<RenewalRatePeriodResponse> periodList = summary.getPeriods() != null
                ? summary.getPeriods().stream()
                        .map(RenewalRatePeriodResponse::from)
                        .collect(Collectors.toList())
                : List.of();
        return RenewalRatesResponse.builder()
                .periods(periodList)
                .overallRenewalRate(summary.getOverallRenewalRate())
                .asOf(summary.getAsOf())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RenewalRatePeriodResponse {
        private String periodKey;
        private long renewalsCount;
        private long expiringCount;
        private Double renewalRate;

        public static RenewalRatePeriodResponse from(RenewalRatePeriod p) {
            return RenewalRatePeriodResponse.builder()
                    .periodKey(p.getPeriodKey())
                    .renewalsCount(p.getRenewalsCount())
                    .expiringCount(p.getExpiringCount())
                    .renewalRate(p.getRenewalRate())
                    .build();
        }
    }
}
