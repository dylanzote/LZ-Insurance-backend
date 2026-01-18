package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.ProviderHealth;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class ProviderHealthResponse {
    private String providerId;
    private String providerName;
    private String circuitState;
    private Double errorRatePercent;
    private Integer averageLatencyMs;
    private Long totalRequests;
    private Long successCount;
    private Long failureCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastCheckedAt;

    private Boolean isHealthy;

    public static ProviderHealthResponse fromDomain(ProviderHealth health) {
        var response = new ProviderHealthResponse();
        BeanUtils.copyProperties(health, response);
        if (health.getCircuitState() != null) {
            response.setCircuitState(health.getCircuitState().name());
        }
        return response;
    }
}
