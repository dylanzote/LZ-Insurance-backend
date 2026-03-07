package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.DevicePlatform;
import com.zote.notification.service.domain.model.*;
import com.zote.notification.service.domain.ports.inbound.ManageDevicesPort;
import com.zote.notification.service.domain.ports.outbound.repository.DeviceRepositoryPort;
import com.zote.notification.service.domain.ports.outbound.service.TopicSubscriptionPort;
import com.zote.notification.service.domain.support.DeviceSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManageDevicesImpl implements ManageDevicesPort {

    private final DeviceRepositoryPort deviceRepository;
    private final DeviceSupport deviceSupport;
    private final TopicSubscriptionPort topicSubscriptionPort;

    @Override
    public UserDevice registerDevice(RegisterDeviceData registerDeviceData) {
        log.info("Registering device for user: {}, deviceId: {}", registerDeviceData.getUserId(), registerDeviceData.getDeviceId());
        return deviceRepository.findByUserIdAndDeviceId(registerDeviceData.getUserId(), registerDeviceData.getDeviceId())
            .map(existing -> updateExistingDevice(existing, registerDeviceData))
            .orElseGet(() -> createNewDevice(registerDeviceData));
    }

    @Override
    public void unregisterDevice(String deviceId) {
        log.info("Unregistering device: {}", deviceId);
        UserDevice device = deviceRepository.findByDeviceId(deviceId);
        deviceRepository.deleteById(device.getId());
        log.info("Device unregistered successfully: {}", deviceId);
    }

    @Override
    public void updateDeviceToken(UpdateDeviceTokenData deviceTokenData) {
        log.info("Updating device token for device: {}", deviceTokenData.getDeviceId());

        UserDevice device = deviceRepository.findByDeviceId(deviceTokenData.getDeviceId());
        String oldToken = device.getPushToken();
        String newToken = deviceTokenData.getNewToken();

        setDeviceToken(device, newToken);
        device.setLastSeenAt(LocalDateTime.now());
        deviceRepository.save(device);

        updateTopicSubscriptions(device, oldToken, newToken);
        log.info("Device token updated successfully for device: {}", deviceTokenData.getDeviceId());
    }


    @Override
    public void subscribeToTopic(SubscribeToTopicData subscribeToTopicData) {
        log.info("Subscribing device {} to topic: {}", subscribeToTopicData.getDeviceId(), subscribeToTopicData.getTopic());

        UserDevice device = deviceRepository.findById(subscribeToTopicData.getDeviceId());

        // Add topic subscription
        deviceRepository.addTopicSubscription(subscribeToTopicData.getDeviceId(), subscribeToTopicData.getTopic());

        // Subscribe to push notification topic (implementation depends on provider)
        subscribeToPushTopic(device.getPushToken(), subscribeToTopicData.getTopic(), device.getPlatform());

        log.info("Device {} subscribed to topic {} successfully",
            subscribeToTopicData.getDeviceId(), subscribeToTopicData.getTopic());
    }

    @Override
    public void unsubscribeFromTopic(UnsubscribeFromTopicData command) {
        log.info("Unsubscribing device {} from topic: {}", command.getDeviceId(), command.getTopic());

        UserDevice device = deviceRepository.findById(command.getDeviceId());

        // Remove topic subscription
        deviceRepository.removeTopicSubscription(command.getDeviceId(), command.getTopic());

        // Unsubscribe from push notification topic
        unsubscribeFromPushTopic(device.getPushToken(), command.getTopic(), device.getPlatform());

        log.info("Device {} unsubscribed from topic {} successfully",
            command.getDeviceId(), command.getTopic());
    }

    @Override
    public List<UserDevice> getUserDevices(String userId) {
        return deviceRepository.findByUserId(userId);
    }

    @Override
    public List<UserDevice> getActiveUserDevices(String userId) {
        return deviceRepository.findActiveDevicesByUserId(userId);
    }

    // P3.6: Enhanced Device Management Implementation
    @Override
    public List<UserDevice> getMyDevices() {
        // Get current user from security context
        // For now, return empty list as we need user context integration
        log.warn("getMyDevices requires user context integration");
        return List.of();
    }

    @Override
    public List<DeviceActivity> getDeviceActivity(String deviceId) {
        log.info("Getting activity for device: {}", deviceId);
        
        // Return mock activity logs
        // In production, this would query a device_activity table
        return List.of(
            new DeviceActivity(
                UUID.randomUUID().toString(),
                deviceId,
                "REGISTERED",
                LocalDateTime.now().minusDays(7),
                Map.of("platform", "ANDROID", "osVersion", "13")
            ),
            new DeviceActivity(
                UUID.randomUUID().toString(),
                deviceId,
                "TOKEN_UPDATED",
                LocalDateTime.now().minusDays(2),
                Map.of("reason", "token_refresh")
            ),
            new DeviceActivity(
                UUID.randomUUID().toString(),
                deviceId,
                "NOTIFICATION_RECEIVED",
                LocalDateTime.now().minusHours(3),
                Map.of("notificationId", UUID.randomUUID().toString())
            )
        );
    }

    @Override
    public void updateDeviceMetadata(String deviceId, Map<String, Object> metadata) {
        log.info("Updating metadata for device: {}", deviceId);
        // This would update device metadata in production
        // Requires adding metadata field to UserDevice model
        log.warn("Device metadata update not fully implemented - requires UserDevice model enhancement");
    }

    @Override
    public void deactivateDevice(String deviceId) {
        log.info("Deactivating device: {}", deviceId);
        deviceRepository.deleteById(deviceId);
        log.info("Device deactivated successfully: {}", deviceId);
    }

    @Override
    public void reactivateDevice(String deviceId) {
        log.info("Reactivating device: {}", deviceId);
        // This would reactivate a deactivated device in production
        // Requires adding active/inactive status to UserDevice model
        log.warn("Device reactivation not fully implemented - requires UserDevice model enhancement");
    }

    private UserDevice updateExistingDevice(UserDevice userDevice, RegisterDeviceData registerDeviceData) {
        log.info("Updating existing device: {}", userDevice.getId());

        var modifiedUserDevice = deviceSupport.updateUserDevice(userDevice, registerDeviceData);

        // Add initial topics if provided
        if (registerDeviceData.getInitialTopics() != null) {
            registerDeviceData.getInitialTopics().forEach(topic ->
                deviceRepository.addTopicSubscription(modifiedUserDevice.getId(), topic));

            // Subscribe to push topics
            registerDeviceData.getInitialTopics().forEach(topic ->
                subscribeToPushTopic(registerDeviceData.getPushToken(), topic, registerDeviceData.getPlatform()));
        }
        return deviceRepository.save(modifiedUserDevice);
    }

    private UserDevice createNewDevice(RegisterDeviceData registerDeviceData) {
        log.info("Creating new device for user: {}", registerDeviceData.getUserId());

        UserDevice device = deviceSupport.buildUserDevice(registerDeviceData);

        UserDevice saved = deviceRepository.save(device);

        // Add initial topics if provided
        if (registerDeviceData.getInitialTopics() != null) {
            registerDeviceData.getInitialTopics().forEach(topic ->
                deviceRepository.addTopicSubscription(saved.getId(), topic));

            // Subscribe to push topics
            registerDeviceData.getInitialTopics().forEach(topic ->
                subscribeToPushTopic(registerDeviceData.getPushToken(), topic, registerDeviceData.getPlatform()));
        }

        log.info("New device created successfully: {}", saved.getId());
        return saved;
    }

    private void updateTopicSubscriptions(UserDevice device, String oldToken, String newToken) {
        log.info("Updating topic subscriptions from old token to new token for device: {}", device.getId());
        try {
            Set<String> topics = device.getSubscribedTopics();

            if (topics != null && !topics.isEmpty()) {
                log.info("Updating {} topic subscriptions for device: {}", topics.size(), device.getId());

                for (String topic : topics) {
                    topicSubscriptionPort.unsubscribeFromTopic(oldToken, topic, device.getPlatform());
                    topicSubscriptionPort.subscribeToTopic(newToken, topic, device.getPlatform());
                }

                log.info("Successfully updated topic subscriptions for device: {}", device.getId());
            } else {
                log.debug("No topics to update for device: {}", device.getId());
            }
        } catch (Exception e) {
            log.warn("Error updating topic subscriptions for device: {}", device.getId(), e);
        }
    }

    private void subscribeToPushTopic(String token, String topic, DevicePlatform platform) {
        log.debug("Subscribing token {} to topic {} on platform {}", token, topic, platform);
        topicSubscriptionPort.subscribeToTopic(token, topic, platform);
    }

    private void unsubscribeFromPushTopic(String token, String topic, DevicePlatform platform) {
        log.debug("Unsubscribing token {} from topic {} on platform {}", token, topic, platform);
        topicSubscriptionPort.unsubscribeFromTopic(token, topic, platform);
    }

    private void setDeviceToken(UserDevice device, String pushToken) {
        boolean isExpo = deviceSupport.isExpoPlatform(device.getPlatform());
        if (isExpo) {
            device.setPushToken(pushToken);
        } else {
            if (deviceSupport.isAndroid(device.getPlatform())) {
                device.setFcmToken(pushToken);
                device.setPushToken(pushToken);
            } else if (deviceSupport.isIOS(device.getPlatform())) {
                device.setApnsToken(pushToken);
                device.setPushToken(pushToken);
            } else {
                device.setPushToken(pushToken);
            }
        }
    }

    private String generateDeviceId() {
        return "dev-" + UUID.randomUUID();
    }
}
