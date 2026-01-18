package com.zote.notification.service.domain.ports.inbound;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.UpdatePreferencesData;
import com.zote.notification.service.domain.model.UserNotificationPreferences;

import java.time.LocalTime;
import java.util.List;

public interface ManagePreferencesPort {
    UserNotificationPreferences updatePreferences(UpdatePreferencesData data);
    UserNotificationPreferences getPreferences(String userId);

    List<UserNotificationPreferences> getUserChannelPreferences(String userId);
    void enableChannel(String userId, NotificationChannel channel);
    void disableChannel(String userId, NotificationChannel channel);
    void setLocale(String userId, String locale);
    void setQuietHours(String userId, String startTime, String endTime);

    void setTimezone(String userId, String timezone);
}
