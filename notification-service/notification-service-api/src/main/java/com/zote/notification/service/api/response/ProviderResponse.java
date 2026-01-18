package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationProvider;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ProviderResponse {
    private String id;
    private String name;
    private String channel;
    private String providerType;
    private Map<String, Object> config;
    private Integer priority;
    private Boolean isActive;
    private Integer maxRatePerMinute;
    private Boolean circuitBreakerEnabled;
    private Integer circuitBreakerThreshold;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static ProviderResponse toResponse(NotificationProvider notificationProvider) {
        ProviderResponse providerResponse = new ProviderResponse();
        BeanUtils.copyProperties(notificationProvider, providerResponse);
        return providerResponse;
    }
}
