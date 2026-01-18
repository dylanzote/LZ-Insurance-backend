package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationProvider {
    private String id;
    private String name;
    private NotificationChannel channel;
    private ProviderType providerType;
    private Map<String, Object> config;
    private Integer priority;
    private Boolean isActive;
    private Integer maxRatePerMinute;
    private Boolean circuitBreakerEnabled;
    private Integer circuitBreakerThreshold;
    private ProviderHealthMetrics healthMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
