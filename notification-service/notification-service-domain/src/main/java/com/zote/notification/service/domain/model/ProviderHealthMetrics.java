package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.CircuitState;
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
public class ProviderHealthMetrics {
    private Long id;
    private String providerId;
    private LocalDateTime timestamp;
    private Integer successCount;
    private Integer failureCount;
    private Integer totalRequests;
    private Integer averageLatencyMs;
    private BigDecimal errorRatePercent;
    private CircuitState circuitState;
    private LocalDateTime lastSuccessAt;
    private LocalDateTime lastFailureAt;
}
