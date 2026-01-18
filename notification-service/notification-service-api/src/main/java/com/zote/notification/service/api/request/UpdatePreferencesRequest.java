package com.zote.notification.service.api.request;

import com.zote.notification.service.domain.model.UpdatePreferencesData;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@Data
public class UpdatePreferencesRequest {
    private Map<com.zote.common.utils.enums.NotificationChannel, Boolean> channelEnabled;
    private String locale;
    private String timezone;
    private String quietHoursStart; // HH:mm format - will be parsed in domain layer
    private String quietHoursEnd; // HH:mm format - will be parsed in domain layer
    private Map<String, Object> customPreferences;

    public UpdatePreferencesData toUpdatePreferencesData(String userId) {
        var data = new UpdatePreferencesData();
        BeanUtils.copyProperties(this, data);
        data.setUserId(userId);
        // Time parsing will be done in domain layer
        return data;
    }
}

