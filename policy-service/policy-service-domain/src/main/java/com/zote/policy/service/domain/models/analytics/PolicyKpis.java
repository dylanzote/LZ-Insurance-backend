package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 16.2 - KPIs for business analyst: active policies, total premium, average premium.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyKpis {
    private long activePolicies;
    private BigDecimal totalPremium;
    private BigDecimal averagePremium;
    private java.time.LocalDateTime asOf;
}
