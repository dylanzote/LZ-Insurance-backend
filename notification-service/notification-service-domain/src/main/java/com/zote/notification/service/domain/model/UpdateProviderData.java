package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProviderData {
    private String providerId;
    private String name;
    private Map<String, Object> config;
    private Integer priority;
    private Boolean isActive;
    private Integer maxRatePerMinute;
    private Boolean circuitBreakerEnabled;
    private Integer circuitBreakerThreshold;

}
