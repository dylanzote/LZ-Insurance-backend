package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.UserDevice;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DeviceResponse {
    private String id;
    private String deviceId;
    private String platform;
    private String platformVersion;
    private String appVersion;
    private String deviceModel;
    private String deviceLanguage;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSeenAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<String> subscribedTopics;

    public static DeviceResponse toDeviceResponse(UserDevice userDevice) {
        var deviceResponse = new DeviceResponse();
        BeanUtils.copyProperties(userDevice, deviceResponse);
        return deviceResponse;
    }
}
