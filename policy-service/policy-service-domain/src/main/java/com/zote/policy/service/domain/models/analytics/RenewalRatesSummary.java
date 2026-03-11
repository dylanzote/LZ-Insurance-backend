package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 16.3 - Renewal rates over time for customer retention measurement.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewalRatesSummary {
    /** Renewals per period (e.g. per month) */
    private List<RenewalRatePeriod> periods;
    /** Overall renewal rate (renewals / policies expiring in period) if calculable */
    private Double overallRenewalRate;
    private java.time.LocalDateTime asOf;
}
