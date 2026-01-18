package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProviderData {
    private String name;
    private NotificationChannel channel;
    private ProviderType providerType;
    private Map<String, Object> config;
    private Integer priority;
    private Integer maxRatePerMinute;
    private Boolean circuitBreakerEnabled;
    private Integer circuitBreakerThreshold;

}
