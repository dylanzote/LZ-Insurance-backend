package com.zote.notification.service.domain.support;

import com.zote.common.utils.enums.DevicePlatform;
import com.zote.notification.service.domain.model.RegisterDeviceData;
import com.zote.notification.service.domain.model.UserDevice;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class DeviceSupport {
    
    public UserDevice buildUserDevice(RegisterDeviceData registerUserDeviceData) {
        log.info("building new device for user: {}", registerUserDeviceData.getUserId());
        
        boolean isExpo = isExpoPlatform(registerUserDeviceData.getPlatform());
        String pushToken = registerUserDeviceData.getPushToken();
        
        UserDevice.UserDeviceBuilder builder = UserDevice.builder()
            .id(generateDeviceId())
            .userId(registerUserDeviceData.getUserId())
            .deviceId(registerUserDeviceData.getDeviceId())
            .platform(registerUserDeviceData.getPlatform())
            .platformVersion(registerUserDeviceData.getPlatformVersion())
            .appVersion(registerUserDeviceData.getAppVersion())
            .pushToken(pushToken)
            .deviceModel(registerUserDeviceData.getDeviceModel())
            .deviceLanguage(registerUserDeviceData.getDeviceLanguage())
            .isActive(true)
            .lastSeenAt(LocalDateTime.now())
            .subscribedTopics(registerUserDeviceData.getInitialTopics() != null ?
                registerUserDeviceData.getInitialTopics() : Set.of());
        
        // Set platform-specific tokens
        if (isExpo) {
            // Expo uses a single push token
            builder.pushToken(pushToken);
        } else {
            // Native platforms: separate FCM and APNS tokens
            if (isAndroid(registerUserDeviceData.getPlatform())) {
                builder.fcmToken(pushToken);
            } else if (isIOS(registerUserDeviceData.getPlatform())) {
                builder.apnsToken(pushToken);
            } else {
                builder.pushToken(pushToken); // Web or other
            }
        }
        
        return builder.build();
    }
    
    public boolean isExpoPlatform(DevicePlatform platform) {
        return platform == DevicePlatform.EXPO_IOS ||
               platform == DevicePlatform.EXPO_ANDROID ||
               platform == DevicePlatform.EXPO_WEB;
    }
    
    public boolean isAndroid(DevicePlatform platform) {
        return platform == DevicePlatform.ANDROID;
    }
    
    public boolean isIOS(DevicePlatform platform) {
        return platform == DevicePlatform.IOS;
    }
    
    public UserDevice updateUserDevice(UserDevice userDevice, RegisterDeviceData registerDeviceData) {
        log.info("Updating existing device: {}", userDevice.getDeviceId());
        
        boolean isExpo = isExpoPlatform(registerDeviceData.getPlatform());
        String pushToken = registerDeviceData.getPushToken();
        
        userDevice.setPlatform(registerDeviceData.getPlatform());
        userDevice.setPlatformVersion(registerDeviceData.getPlatformVersion());
        userDevice.setAppVersion(registerDeviceData.getAppVersion());
        userDevice.setDeviceModel(registerDeviceData.getDeviceModel());
        userDevice.setDeviceLanguage(registerDeviceData.getDeviceLanguage());
        userDevice.setIsActive(true);
        userDevice.setLastSeenAt(LocalDateTime.now());
        
        // Update tokens based on platform
        if (isExpo) {
            userDevice.setPushToken(pushToken);
            userDevice.setFcmToken(null);
            userDevice.setApnsToken(null);
        } else {
            if (isAndroid(registerDeviceData.getPlatform())) {
                userDevice.setFcmToken(pushToken);
                userDevice.setPushToken(pushToken);
                userDevice.setApnsToken(null);
            } else if (isIOS(registerDeviceData.getPlatform())) {
                userDevice.setApnsToken(pushToken);
                userDevice.setPushToken(pushToken);
                userDevice.setFcmToken(null);
            } else {
                userDevice.setPushToken(pushToken);
            }
        }
        
        return userDevice;
    }
    
    private String generateDeviceId() {
        return "dev-" + UUID.randomUUID();
    }
}
