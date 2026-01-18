package com.zote.notification.service.infrastructure.adapters.providers.push;

import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.*;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.ports.outbound.repository.DeviceRepositoryPort;
import com.zote.notification.service.infrastructure.adapters.providers.NotificationProviderAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.providers.fcm.enabled", havingValue = "true")
public class FcmPushProvider implements NotificationProviderAdapter {

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceRepositoryPort deviceRepository;

    @Override
    public NotificationResult send(Notification notification) {
        List<String> deviceTokens = deviceRepository
                .findActiveDevicesByUserId(notification.getUserId())
                .stream()
                .map(device -> device.getPushToken() != null ? device.getPushToken() : device.getFcmToken())
                .filter(token -> token != null && !token.isEmpty())
                .toList();

        if (deviceTokens.isEmpty()) {
            return NotificationResult.failed("No active devices found for user");
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(com.google.firebase.messaging.Notification.builder()
                        .setTitle(notification.getTitle())
                        .setBody(notification.getMessage())
                        .build())
                .putAllData(extractPushData(notification.getMetadata()))
                .addAllTokens(deviceTokens)
                .setApnsConfig(getApnsConfig(notification))
                .setAndroidConfig(getAndroidConfig(notification))
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);

            // Handle individual failures
            List<SendResponse> responses = response.getResponses();
            int successCount = response.getSuccessCount();
            int failureCount = response.getFailureCount();

            if (failureCount > 0) {
                for (int i = 0; i < responses.size(); i++) {
                    SendResponse sendResponse = responses.get(i);
                    if (!sendResponse.isSuccessful()) {
                        String failedToken = deviceTokens.get(i);
                        log.warn("Failed to send to token {}: {}",
                                failedToken, sendResponse.getException().getMessage());

                        // Remove invalid tokens
                        if (isTokenInvalid(sendResponse.getException())) {
                            deviceRepository.deactivateToken(failedToken);
                        }
                    }
                }
            }

            return NotificationResult.success(
                    String.format("Sent to %d devices, %d failed", successCount, failureCount));
        } catch (FirebaseMessagingException e) {
            log.error("FCM error", e);
            return NotificationResult.failed("FCM error: " + e.getMessage());
        }
    }

    private Map<String, String> extractPushData(Map<String, Object> metadata) {
        if (metadata == null) {
            return Map.of();
        }
        // Extract relevant data for push notification
        return Map.of(
                "type", metadata.getOrDefault("type", "notification").toString(),
                "channel", metadata.getOrDefault("channel", "push").toString()
        );
    }

    private ApnsConfig getApnsConfig(Notification notification) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setBadge(1)
                        .setSound("default")
                        .build())
                .build();
    }

    private AndroidConfig getAndroidConfig(Notification notification) {
        return AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                        .setIcon("notification_icon")
                        .setColor("#FF0000")
                        .build())
                .build();
    }

    private boolean isTokenInvalid(FirebaseMessagingException e) {
        return e.getErrorCode() == ErrorCode.INVALID_ARGUMENT ||
                e.getErrorCode() == ErrorCode.NOT_FOUND;
    }

    @Override
    public String getName() {
        return "Firebase Cloud Messaging (FCM)";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.FCM;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public boolean isConfigured() {
        return firebaseMessaging != null;
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("fcm")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via Firebase Admin SDK initialization
        log.debug("FCM provider configuration updated");
    }
}
