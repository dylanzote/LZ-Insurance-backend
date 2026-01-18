package com.zote.notification.service.api.usecase;

import com.zote.notification.service.api.controller.DeviceApi;
import com.zote.notification.service.api.request.RegisterDeviceRequest;
import com.zote.notification.service.api.request.UpdateDeviceTokenRequest;
import com.zote.notification.service.api.response.DeviceResponse;
import com.zote.notification.service.domain.model.*;
import com.zote.notification.service.domain.ports.inbound.ManageDevicesPort;
import com.zote.notification.service.domain.usecases.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DeviceService implements DeviceApi {

    private final ManageDevicesPort manageDevicesPort;
    private final UserValidationService userValidationService;

    @Override
    public DeviceResponse registerDevice(RegisterDeviceRequest request) {
        log.info("API: Registering device for user: {}", request.getUserId());
        
        // Validate user exists
        userValidationService.validateAndGetUser(request.getUserId());
        
        var deviceData = request.toRegisterDeviceData();
        return DeviceResponse.toDeviceResponse(manageDevicesPort.registerDevice(deviceData));
    }

    @Override
    public void updateDeviceToken(UpdateDeviceTokenRequest request) {
        log.info("API: Updating device token for device: {}", request.getDeviceId());
        manageDevicesPort.updateDeviceToken(request.toUpdateDeviceTokenData());
    }

    @Override
    public List<DeviceResponse> getUserDevices(String userId) {
        log.info("API: Getting devices for user: {}", userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        return manageDevicesPort.getUserDevices(userId).stream()
            .map(DeviceResponse::toDeviceResponse)
            .toList();
    }

    @Override
    public void subscribeToTopic(String deviceId, String topic) {
        log.info("API: Subscribing device {} to topic: {}", deviceId, topic);
        var subscribeToTopicData = SubscribeToTopicData.builder()
            .deviceId(deviceId)
            .topic(topic)
            .build();
        manageDevicesPort.subscribeToTopic(subscribeToTopicData);
    }

    @Override
    public void unsubscribeFromTopic(String deviceId, String topic) {
        log.info("API: Unsubscribing device {} from topic: {}", deviceId, topic);
        var unsubscribeFromTopicData = UnsubscribeFromTopicData.builder()
            .deviceId(deviceId)
            .topic(topic)
            .build();
        manageDevicesPort.unsubscribeFromTopic(unsubscribeFromTopicData);
    }

    @Override
    public void unregisterDevice(String deviceId) {
        log.info("API: Unregistering device: {}", deviceId);
        manageDevicesPort.unregisterDevice(deviceId);
    }

    // P3.6: Enhanced Device Management API
    @Override
    public List<DeviceResponse> getMyDevices() {
        log.info("API: Getting current user's devices - P3.6 feature stub");
        log.warn("P3.6: getMyDevices requires user context integration");
        return List.of();
    }

    @Override
    public List<Object> getDeviceActivity(String deviceId) {
        log.info("API: Getting activity for device: {}", deviceId);
        return manageDevicesPort.getDeviceActivity(deviceId).stream()
            .map(activity -> (Object) activity)
            .toList();
    }

    @Override
    public void updateDeviceMetadata(String deviceId, java.util.Map<String, Object> metadata) {
        log.info("API: Updating metadata for device: {}", deviceId);
        manageDevicesPort.updateDeviceMetadata(deviceId, metadata);
    }

    @Override
    public void deactivateDevice(String deviceId) {
        log.info("API: Deactivating device: {}", deviceId);
        manageDevicesPort.deactivateDevice(deviceId);
    }

    @Override
    public void reactivateDevice(String deviceId) {
        log.info("API: Reactivating device: {}", deviceId);
        manageDevicesPort.reactivateDevice(deviceId);
    }
}
