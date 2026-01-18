package com.zote.notification.service.infrastructure.adapters.providers.push;

import com.zote.common.utils.enums.DevicePlatform;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.common.utils.request.HttpService;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.ports.outbound.repository.DeviceRepositoryPort;
import com.zote.notification.service.infrastructure.adapters.providers.NotificationProviderAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Expo Push Notification Provider
 * Supports sending push notifications to Expo (React Native) apps
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.providers.expo.enabled", havingValue = "true")
public class ExpoPushProvider implements NotificationProviderAdapter {

    private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send";
    private final DeviceRepositoryPort deviceRepository;
    private final RestTemplate restTemplate;
    private final ExpoPushProviderConfig expoConfig;

    @Override
    public NotificationResult send(Notification notification) {
        log.info("Sending Expo push notification to user: {}", notification.getUserId());

        // Get all active Expo devices for the user
        List<String> expoTokens = deviceRepository
                .findActiveDevicesByUserId(notification.getUserId())
                .stream()
                .filter(device -> {
                    // Check if device is Expo platform
                    return device.getPlatform() != null &&
                            (device.getPlatform() == DevicePlatform.EXPO_IOS ||
                                    device.getPlatform() == DevicePlatform.EXPO_ANDROID ||
                                    device.getPlatform() == DevicePlatform.EXPO_WEB);
                })
                .map(device -> {
                    // Expo devices use pushToken
                    return device.getPushToken() != null ? device.getPushToken() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (expoTokens.isEmpty()) {
            log.warn("No active Expo devices found for user: {}", notification.getUserId());
            return NotificationResult.failed("No active Expo devices found for user");
        }

        try {
            // Prepare Expo push messages
            List<Map<String, Object>> messages = expoTokens.stream()
                    .map(token -> buildExpoMessage(token, notification))
                    .collect(Collectors.toList());

            // Send to Expo Push API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");
            headers.set("Accept-Encoding", "gzip, deflate");


            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messages, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    EXPO_PUSH_API_URL,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // Expo API returns data as a list of response objects
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                if (data != null && !data.isEmpty()) {
                    long successCount = data.stream()
                            .filter(item -> "ok".equals(item.get("status")))
                            .count();

                    long failureCount = data.size() - successCount;

                    // Log failures
                    data.stream()
                            .filter(item -> !"ok".equals(item.get("status")))
                            .forEach(item -> log.warn("Expo push failed: {}", item.get("message")));

                    return NotificationResult.success(
                            String.format("Sent to %d Expo devices, %d failed", successCount, failureCount)
                    );
                }
            }

            return NotificationResult.success("Expo push notification sent");
        } catch (Exception e) {
            log.error("Error sending Expo push notification", e);
            return NotificationResult.failed("Expo push error: " + e.getMessage());
        }
    }

    private Map<String, Object> buildExpoMessage(String token, Notification notification) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", token);
        message.put("title", notification.getTitle());
        message.put("body", notification.getMessage());
        message.put("sound", "default");
        message.put("priority", "high");
        message.put("channelId", "default");

        // Add data payload
        if (notification.getMetadata() != null) {
            message.put("data", notification.getMetadata());
        }

        // Add badge count if available
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("badge")) {
            message.put("badge", notification.getMetadata().get("badge"));
        }

        return message;
    }

    @Override
    public ProviderType getType() {
        return ProviderType.EXPO;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public String getName() {
        return "Expo Push Notifications";
    }

    @Override
    public boolean isConfigured() {
        return expoConfig != null && expoConfig.isEnabled();
    }

    @Override
    public ProviderHealth healthCheck() {
        // Simple health check - in production, you might ping Expo API
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("expo")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via ExpoPushProviderConfig
        log.debug("Expo provider configuration updated");
    }

}

