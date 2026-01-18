package com.zote.notification.service.api.request;

import com.zote.common.utils.enums.DevicePlatform;

import java.util.Set;

public class RegisterDeviceCommand {
    private String userId;
    private String deviceId;
    private DevicePlatform platform;
    private String platformVersion;
    private String appVersion;
    private String pushToken;
    private String deviceModel;
    private String deviceLanguage;
    private Set<String> initialTopics;
}
