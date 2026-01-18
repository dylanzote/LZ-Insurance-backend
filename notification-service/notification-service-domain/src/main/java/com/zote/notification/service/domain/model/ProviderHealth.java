package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.CircuitState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderHealth {
    private String providerId;
    private String providerName;
    private CircuitState circuitState;
    private Double errorRatePercent;
    private Integer averageLatencyMs;
    private Long totalRequests;
    private Long successCount;
    private Long failureCount;
    private LocalDateTime lastCheckedAt;
    private Boolean isHealthy;
}
