package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.PolicyKpis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyKpisResponse {
    private long activePolicies;
    private BigDecimal totalPremium;
    private BigDecimal averagePremium;
    private LocalDateTime asOf;

    public static PolicyKpisResponse from(PolicyKpis kpis) {
        return PolicyKpisResponse.builder()
                .activePolicies(kpis.getActivePolicies())
                .totalPremium(kpis.getTotalPremium())
                .averagePremium(kpis.getAveragePremium())
                .asOf(kpis.getAsOf())
                .build();
    }
}
