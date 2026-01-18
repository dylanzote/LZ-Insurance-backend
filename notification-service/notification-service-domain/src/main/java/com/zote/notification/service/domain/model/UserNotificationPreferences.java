package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationPreferences {
    private String id;
    private String userId;
    private NotificationChannel channel;
    private Boolean enabled;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
    private String locale;
    private String timezone;
    private Map<String, Object> preferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
