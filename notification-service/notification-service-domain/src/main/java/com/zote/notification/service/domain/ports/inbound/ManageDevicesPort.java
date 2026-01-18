package com.zote.notification.service.domain.ports.inbound;

import com.zote.notification.service.domain.model.*;

import java.util.List;
import java.util.Map;

public interface ManageDevicesPort {
    UserDevice registerDevice(RegisterDeviceData registerDeviceData);
    void unregisterDevice(String deviceId);
    void updateDeviceToken(UpdateDeviceTokenData updateDeviceTokenData);
    void subscribeToTopic(SubscribeToTopicData subscribeToTopicData);
    void unsubscribeFromTopic(UnsubscribeFromTopicData unsubscribeFromTopicData);
    List<UserDevice> getUserDevices(String userId);

    List<UserDevice> getActiveUserDevices(String userId);

    // P3.6: Enhanced Device Management
    List<UserDevice> getMyDevices();
    List<DeviceActivity> getDeviceActivity(String deviceId);
    void updateDeviceMetadata(String deviceId, Map<String, Object> metadata);
    void deactivateDevice(String deviceId);
    void reactivateDevice(String deviceId);
    
    record DeviceActivity(
        String activityId,
        String deviceId,
        String action,
        java.time.LocalDateTime timestamp,
        Map<String, Object> metadata
    ) {}
}
