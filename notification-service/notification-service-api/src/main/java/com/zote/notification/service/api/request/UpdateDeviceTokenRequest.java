package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.UpdateDeviceTokenData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UpdateDeviceTokenRequest {
    private String deviceId;
    private String newToken;
    private String userId;

    public UpdateDeviceTokenData toUpdateDeviceTokenData() {
        var updateDeviceTokenData = new UpdateDeviceTokenData();
        BeanUtils.copyProperties(this, updateDeviceTokenData);
        return updateDeviceTokenData;
    }
}
