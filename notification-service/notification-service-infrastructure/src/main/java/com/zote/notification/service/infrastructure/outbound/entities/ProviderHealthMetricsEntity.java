package com.zote.notification.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.common.utils.enums.CircuitState;
import com.zote.notification.service.domain.model.ProviderHealthMetrics;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Entity
@Table(name = "provider_health_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProviderHealthMetricsEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    private LocalDateTime timestamp;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "failure_count")
    private Integer failureCount;

    @Column(name = "total_requests")
    private Integer totalRequests;

    @Column(name = "average_latency_ms")
    private Integer averageLatencyMs;

    @Column(name = "error_rate_percent")
    private Double errorRatePercent;

    @Enumerated(EnumType.STRING)
    @Column(name = "circuit_state")
    private CircuitState circuitState;

    @Column(name = "last_success_at")
    private LocalDateTime lastSuccessAt;

    @Column(name = "last_failure_at")
    private LocalDateTime lastFailureAt;

    public static ProviderHealthMetricsEntity toEntity(ProviderHealthMetrics providerHealthMetrics) {
        ProviderHealthMetricsEntity entity = new ProviderHealthMetricsEntity();
        BeanUtils.copyProperties(providerHealthMetrics, entity);
        return entity;
    }

    public ProviderHealthMetrics toDto() {
        ProviderHealthMetrics providerHealthMetrics = new ProviderHealthMetrics();
        BeanUtils.copyProperties(this, providerHealthMetrics);
        return providerHealthMetrics;
    }
}
