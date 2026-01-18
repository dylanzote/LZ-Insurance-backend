package com.zote.notification.service.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zote.notification.service.domain.model.UserNotificationPreferences;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Data
public class UserPreferencesResponse {
    private String userId;
    private String locale;
    private String timezone;
    private String quietHoursStart;
    private String quietHoursEnd;
    private Map<String, Boolean> channelEnabled;
    private Map<String, Object> customPreferences;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserPreferencesResponse fromDomain(UserNotificationPreferences preferences) {
        var response = new UserPreferencesResponse();
        BeanUtils.copyProperties(preferences, response);
        if (preferences.getQuietHoursStart() != null) {
            response.setQuietHoursStart(preferences.getQuietHoursStart().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        if (preferences.getQuietHoursEnd() != null) {
            response.setQuietHoursEnd(preferences.getQuietHoursEnd().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        return response;
    }
}
