package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.PortfolioSnapshot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSnapshotResponse {
    private long totalPolicies;
    private long activePolicies;
    private long expiredPolicies;
    private long cancelledPolicies;
    private long suspendedPolicies;
    private long pendingPolicies;
    private Map<String, Long> countByStatus;
    private BigDecimal totalPremium;
    private LocalDateTime asOf;

    public static PortfolioSnapshotResponse from(PortfolioSnapshot snapshot) {
        return PortfolioSnapshotResponse.builder()
                .totalPolicies(snapshot.getTotalPolicies())
                .activePolicies(snapshot.getActivePolicies())
                .expiredPolicies(snapshot.getExpiredPolicies())
                .cancelledPolicies(snapshot.getCancelledPolicies())
                .suspendedPolicies(snapshot.getSuspendedPolicies())
                .pendingPolicies(snapshot.getPendingPolicies())
                .countByStatus(snapshot.getCountByStatus())
                .totalPremium(snapshot.getTotalPremium())
                .asOf(snapshot.getAsOf())
                .build();
    }
}
