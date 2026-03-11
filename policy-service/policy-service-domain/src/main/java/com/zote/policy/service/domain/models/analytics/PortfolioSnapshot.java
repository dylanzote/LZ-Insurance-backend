package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 16.1 - Snapshot of the entire policy portfolio for business performance overview.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSnapshot {
    private long totalPolicies;
    private long activePolicies;
    private long expiredPolicies;
    private long cancelledPolicies;
    private long suspendedPolicies;
    private long pendingPolicies;
    /** Count per status (ACTIVE, EXPIRED, CANCELLED, etc.) */
    private Map<String, Long> countByStatus;
    /** Total premium across all active policies */
    private BigDecimal totalPremium;
    /** Snapshot timestamp */
    private java.time.LocalDateTime asOf;
}
