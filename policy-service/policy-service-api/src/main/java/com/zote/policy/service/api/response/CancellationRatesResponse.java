package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.CancellationRatePeriod;
import com.zote.policy.service.domain.models.analytics.CancellationRatesSummary;
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
public class CancellationRatesResponse {
    private List<CancellationRatePeriodResponse> periods;
    private long totalCancellations;
    private LocalDateTime asOf;

    public static CancellationRatesResponse from(CancellationRatesSummary summary) {
        List<CancellationRatePeriodResponse> periodList = summary.getPeriods() != null
                ? summary.getPeriods().stream()
                        .map(CancellationRatePeriodResponse::from)
                        .collect(Collectors.toList())
                : List.of();
        return CancellationRatesResponse.builder()
                .periods(periodList)
                .totalCancellations(summary.getTotalCancellations())
                .asOf(summary.getAsOf())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancellationRatePeriodResponse {
        private String periodKey;
        private long cancellationsCount;

        public static CancellationRatePeriodResponse from(CancellationRatePeriod p) {
            return CancellationRatePeriodResponse.builder()
                    .periodKey(p.getPeriodKey())
                    .cancellationsCount(p.getCancellationsCount())
                    .build();
        }
    }
}
