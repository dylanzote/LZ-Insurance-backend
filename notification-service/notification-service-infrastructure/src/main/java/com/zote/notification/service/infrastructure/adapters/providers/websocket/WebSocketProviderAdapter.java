package com.zote.notification.service.infrastructure.adapters.providers.websocket;

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
 * Provider adapter for WEB_SOCKET channel.
 * Handles real-time web notifications via WebSocket.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketProviderAdapter implements NotificationProviderAdapter {

    private final WebSocketServicePort webSocketService;

    @Override
    public NotificationResult send(Notification notification) {
        try {
            log.info("Sending WebSocket notification to user: {}", notification.getUserId());
            
            webSocketService.sendNotificationToUser(notification.getUserId(), notification);
            
            return NotificationResult.success(notification.getId());
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user {}: {}", 
                notification.getUserId(), e.getMessage(), e);
            return NotificationResult.failed("WebSocket error: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "WebSocket Notification Service";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.GENERIC_WEBHOOK;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.WEB_SOCKET;
    }

    @Override
    public boolean isConfigured() {
        return webSocketService != null;
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("websocket")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        log.debug("WebSocket provider configuration updated");
    }
}
