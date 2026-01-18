package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterDeviceData {
    @NotBlank
    private String userId;

    @NotBlank
    private String deviceId;

    @NotNull
    private DevicePlatform platform;

    private String platformVersion;

    private String appVersion;

    @NotBlank
    private String pushToken;

    private String deviceModel;

    private String deviceLanguage;

    private Set<String> initialTopics;
}
