package com.zote.notification.service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceTokenData {
    private String deviceId;
    private String userId;
    private String newToken;
}
