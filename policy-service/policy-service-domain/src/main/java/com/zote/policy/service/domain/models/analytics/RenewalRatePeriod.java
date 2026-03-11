package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Renewal count for a single time period.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewalRatePeriod {
    private String periodKey;   // e.g. "2025-01", "2025-W03"
    private long renewalsCount;
    private long expiringCount; // policies expiring in this period (for rate calc)
    private Double renewalRate; // renewalsCount / expiringCount when expiringCount > 0
}
