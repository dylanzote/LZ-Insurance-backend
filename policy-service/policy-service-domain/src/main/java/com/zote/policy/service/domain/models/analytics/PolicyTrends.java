package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 16.6 - Trends over time: new policies, renewals, cancellations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTrends {
    /** Trend points per period (e.g. per month) */
    private List<PolicyTrendPeriod> periods;
    private java.time.LocalDateTime asOf;
}
