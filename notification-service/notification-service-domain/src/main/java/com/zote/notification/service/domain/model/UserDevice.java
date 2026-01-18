package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.DevicePlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice {
    private String id;
    private String userId;
    private String deviceId;
    private DevicePlatform platform;
    private String platformVersion;
    private String appVersion;
    private String pushToken;
    private String fcmToken;
    private String apnsToken;
    private String deviceModel;
    private String deviceLanguage;
    private Boolean isActive;
    private LocalDateTime lastSeenAt;
    private Set<String> subscribedTopics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
