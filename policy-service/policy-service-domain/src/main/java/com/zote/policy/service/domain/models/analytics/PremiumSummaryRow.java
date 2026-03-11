package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Single row in premium summary (e.g. productId + total premium).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumSummaryRow {
    private String dimensionValue;  // productId, branchId, or agentId
    private long policyCount;
    private BigDecimal totalPremium;
}
