package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 16.4 - Cancellation rates for churn pattern analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationRatesSummary {
    /** Cancellations per period (e.g. per month) */
    private List<CancellationRatePeriod> periods;
    private long totalCancellations;
    private java.time.LocalDateTime asOf;
}
