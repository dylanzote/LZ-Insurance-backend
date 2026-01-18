package com.zote.notification.service.infrastructure.adapters.websocket;

import com.zote.common.utils.enums.NotificationStatus;
import com.zote.notification.service.domain.model.Alert;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationMetrics;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.model.WebSocketNotificationMessage;
import com.zote.notification.service.domain.ports.outbound.service.WebSocketServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebSocketServiceAdapter implements WebSocketServicePort {

    private final SimpMessagingTemplate messagingTemplate;

    // User-specific destinations (automatically prefixed with /user/{userId} by Spring)
    private static final String USER_NOTIFICATIONS_DESTINATION = "/queue/notifications";
    private static final String USER_DELIVERY_STATUS_DESTINATION = "/queue/delivery-status";
    
    // Broadcast destinations (for all subscribers)
    private static final String PROVIDER_HEALTH_DESTINATION = "/topic/provider-health";
    private static final String ALERTS_DESTINATION = "/topic/alerts";
    private static final String METRICS_DESTINATION = "/topic/metrics";

    /**
     * Send notification to a specific user
     * Frontend should subscribe to: /user/{userId}/queue/notifications
     */
    @Override
    public void sendNotificationToUser(String userId, Notification notification) {
        try {
            log.info("Sending WebSocket notification to user: {}", userId);

            WebSocketNotificationMessage message = WebSocketNotificationMessage.builder()
                .id(notification.getId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .priority(notification.getPriority())
                .timestamp(notification.getSentAt() != null ? notification.getSentAt() : 
                           notification.getCreatedAt())
                .metadata(notification.getMetadata())
                .build();

            // Use convertAndSendToUser for user-specific messages
            // Spring automatically routes to /user/{userId}/queue/notifications
            messagingTemplate.convertAndSendToUser(userId, USER_NOTIFICATIONS_DESTINATION, message);

            log.info("WebSocket notification sent to user {} at destination /user/{}/{}", userId, userId, USER_NOTIFICATIONS_DESTINATION);

        } catch (Exception e) {
            log.error("Failed to send WebSocket notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Send delivery status update to a specific user
     * Frontend should subscribe to: /user/{userId}/queue/delivery-status
     */
    @Override
    public void sendDeliveryStatus(String userId, String notificationId, NotificationStatus status) {
        try {
            log.debug("Sending delivery status to user {} for notification: {} with status: {}", 
                    userId, notificationId, status);

            var statusUpdate = new DeliveryStatusUpdate(notificationId, status.name());

            // Use convertAndSendToUser for user-specific messages
            // Spring automatically routes to /user/{userId}/queue/delivery-status
            messagingTemplate.convertAndSendToUser(userId, USER_DELIVERY_STATUS_DESTINATION, statusUpdate);

            log.debug("Delivery status sent to user {} at destination /user/{}/{}", 
                    userId, userId, USER_DELIVERY_STATUS_DESTINATION);

        } catch (Exception e) {
            log.error("Failed to send delivery status to user {} for notification {}: {}", 
                    userId, notificationId, e.getMessage(), e);
        }
    }

    @Override
    public void sendProviderHealthUpdate(ProviderHealth health) {
        try {
            log.debug("Sending provider health update for provider: {}", health.getProviderId());

            messagingTemplate.convertAndSend(PROVIDER_HEALTH_DESTINATION, health);

            log.debug("Provider health update sent for provider: {}", health.getProviderId());

        } catch (Exception e) {
            log.error("Failed to send provider health update: {}", e.getMessage(), e);
        }
    }

    @Override
    public void sendAlert(Alert alert) {
        try {
            log.debug("Sending alert: {}", alert.getId());

            messagingTemplate.convertAndSend(ALERTS_DESTINATION, alert);

            log.debug("Alert sent: {}", alert.getId());

        } catch (Exception e) {
            log.error("Failed to send alert {}: {}", alert.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void sendMetricsUpdate(NotificationMetrics metrics) {
        try {
            log.debug("Sending metrics update");

            messagingTemplate.convertAndSend(METRICS_DESTINATION, metrics);

            log.debug("Metrics update sent");

        } catch (Exception e) {
            log.error("Failed to send metrics update: {}", e.getMessage(), e);
        }
    }

    // Inner class for delivery status updates
    private static class DeliveryStatusUpdate {
        private final String notificationId;
        private final String status;

        public DeliveryStatusUpdate(String notificationId, String status) {
            this.notificationId = notificationId;
            this.status = status;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public String getStatus() {
            return status;
        }
    }
}

