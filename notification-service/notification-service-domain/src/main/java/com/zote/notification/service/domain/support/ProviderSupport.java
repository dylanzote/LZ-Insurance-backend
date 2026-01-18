package com.zote.notification.service.domain.support;

import com.zote.notification.service.domain.model.CreateProviderData;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.UpdateProviderData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ProviderSupport {

    public NotificationProvider buildNotificationProvider(CreateProviderData createProviderData) {
        return NotificationProvider.builder()
            .id(generateProviderId())
            .name(createProviderData.getName())
            .channel(createProviderData.getChannel())
            .providerType(createProviderData.getProviderType())
            .config(createProviderData.getConfig())
            .priority(createProviderData.getPriority() != null ? createProviderData.getPriority() : 1)
            .isActive(true)
            .maxRatePerMinute(createProviderData.getMaxRatePerMinute())
            .circuitBreakerEnabled(createProviderData.getCircuitBreakerEnabled() != null ?
                createProviderData.getCircuitBreakerEnabled() : true)
            .circuitBreakerThreshold(createProviderData.getCircuitBreakerThreshold() != null ?
                createProviderData.getCircuitBreakerThreshold() : 50)
            .build();
    }

    public NotificationProvider updateNotificationProvider(UpdateProviderData updateProviderData, NotificationProvider provider) {
        if (updateProviderData.getName() != null) {
            provider.setName(updateProviderData.getName());
        }
        if (updateProviderData.getConfig() != null) {
            provider.setConfig(updateProviderData.getConfig());
        }
        if (updateProviderData.getPriority() != null) {
            provider.setPriority(updateProviderData.getPriority());
        }
        if (updateProviderData.getIsActive() != null) {
            provider.setIsActive(updateProviderData.getIsActive());
        }
        if (updateProviderData.getMaxRatePerMinute() != null) {
            provider.setMaxRatePerMinute(updateProviderData.getMaxRatePerMinute());
        }
        if (updateProviderData.getCircuitBreakerEnabled() != null) {
            provider.setCircuitBreakerEnabled(updateProviderData.getCircuitBreakerEnabled());
        }
        if (updateProviderData.getCircuitBreakerThreshold() != null) {
            provider.setCircuitBreakerThreshold(updateProviderData.getCircuitBreakerThreshold());
        }

        return provider;
    }

    private String generateProviderId() {
        return "provider-" + UUID.randomUUID();
    }
}
