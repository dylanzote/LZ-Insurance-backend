package com.zote.notification.service.infrastructure.adapters.providers.inapp;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.ports.outbound.service.WebSocketServicePort;
import com.zote.notification.service.infrastructure.adapters.providers.NotificationProviderAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Provider adapter for IN_APP channel.
 * Handles in-app messaging between users via WebSocket.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InAppProviderAdapter implements NotificationProviderAdapter {

    private final WebSocketServicePort webSocketService;

    @Override
    public NotificationResult send(Notification notification) {
        try {
            log.debug("Sending in-app notification to user: {}", notification.getUserId());
            
            // Send via WebSocket for in-app messaging
            webSocketService.sendNotificationToUser(notification.getUserId(), notification);
            
            return NotificationResult.success(notification.getId());
        } catch (Exception e) {
            log.error("Failed to send in-app notification to user {}: {}", 
                notification.getUserId(), e.getMessage(), e);
            return NotificationResult.failed("In-app notification error: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "In-App Messaging Service";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.GENERIC_WEBHOOK;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public boolean isConfigured() {
        return webSocketService != null;
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("inapp")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        log.debug("In-app provider configuration updated");
    }
}
