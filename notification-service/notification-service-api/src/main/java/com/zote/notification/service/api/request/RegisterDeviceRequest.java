package com.zote.notification.service.api.request;

import com.zote.common.utils.enums.DevicePlatform;
import com.zote.notification.service.domain.model.RegisterDeviceData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
public class RegisterDeviceRequest {

    @NotBlank
    private String deviceId;

    private String userId;

    @NotNull
    private DevicePlatform platform;

    private String platformVersion;

    private String appVersion;

    @NotBlank
    private String pushToken;

    private String deviceModel;

    private String deviceLanguage;

    private Set<String> initialTopics;

    public RegisterDeviceData toRegisterDeviceData() {
        var registerDeviceData = new RegisterDeviceData();
        BeanUtils.copyProperties(this, registerDeviceData);
        return registerDeviceData;
    }
}
