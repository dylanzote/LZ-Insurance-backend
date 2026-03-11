package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * New, renewed, and cancelled counts for a single time period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyTrendPeriod {
    private String periodKey;   // e.g. "2025-01", "2025-W03"
    private long newPolicies;
    private long renewals;
    private long cancellations;
}
