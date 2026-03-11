package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.PolicyTrendPeriod;
import com.zote.policy.service.domain.models.analytics.PolicyTrends;
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
public class PolicyTrendsResponse {
    private List<PolicyTrendPeriodResponse> periods;
    private LocalDateTime asOf;

    public static PolicyTrendsResponse from(PolicyTrends trends) {
        List<PolicyTrendPeriodResponse> periodList = trends.getPeriods() != null
                ? trends.getPeriods().stream()
                        .map(PolicyTrendPeriodResponse::from)
                        .collect(Collectors.toList())
                : List.of();
        return PolicyTrendsResponse.builder()
                .periods(periodList)
                .asOf(trends.getAsOf())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PolicyTrendPeriodResponse {
        private String periodKey;
        private long newPolicies;
        private long renewals;
        private long cancellations;

        public static PolicyTrendPeriodResponse from(PolicyTrendPeriod p) {
            return PolicyTrendPeriodResponse.builder()
                    .periodKey(p.getPeriodKey())
                    .newPolicies(p.getNewPolicies())
                    .renewals(p.getRenewals())
                    .cancellations(p.getCancellations())
                    .build();
        }
    }
}
