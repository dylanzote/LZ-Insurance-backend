package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePreferencesData {

    @NotBlank
    private String userId;

    private Map<NotificationChannel, Boolean> channelEnabled;

    private String locale;

    private String timezone;

    private String quietHoursStart; // String from API, will be parsed
    private String quietHoursEnd; // String from API, will be parsed
    private LocalTime parsedQuietHoursStart; // Parsed start time
    private LocalTime parsedQuietHoursEnd; // Parsed end time

    private Map<String, Object> customPreferences;
}
