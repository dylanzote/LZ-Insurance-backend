package com.zote.kafka.adapter.event.analytics;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static com.zote.kafka.adapter.models.EventType.ANALYTICS_PORTFOLIO_SNAPSHOT;

/**
 * 16.8 - Analytics event for downstream BI systems.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsPortfolioSnapshotEvent implements DataEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private String correlationId;

    private long totalPolicies;
    private long activePolicies;
    private long expiredPolicies;
    private long cancelledPolicies;
    private long suspendedPolicies;
    private long pendingPolicies;
    private Map<String, Long> countByStatus;
    private BigDecimal totalPremium;

    @Override
    public String getEventType() {
        return ANALYTICS_PORTFOLIO_SNAPSHOT.getCode();
    }
}
