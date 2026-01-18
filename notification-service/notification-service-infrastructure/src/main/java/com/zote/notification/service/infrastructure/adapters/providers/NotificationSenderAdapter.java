package com.zote.notification.service.infrastructure.adapters.providers;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.ports.outbound.service.NotificationSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationSenderAdapter implements NotificationSenderPort {

    private final ProviderFactory providerFactory;

    @Override
    public NotificationResult send(Notification notification, ProviderType providerType, NotificationChannel channel) {
        try {
            NotificationProviderAdapter adapter = providerFactory.getAdapter(providerType, channel);
            log.info("Sending notification {} via provider {} for channel {}", notification.getId(), providerType, channel);
            
            return adapter.send(notification);
        } catch (Exception e) {
            log.error("Failed to send notification {} via provider {}: {}", 
                notification.getId(), providerType, e.getMessage(), e);
            return NotificationResult.failed("Provider error: " + e.getMessage());
        }
    }
}

