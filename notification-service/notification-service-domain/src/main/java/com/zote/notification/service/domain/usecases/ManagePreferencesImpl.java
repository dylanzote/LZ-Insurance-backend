package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.UpdatePreferencesData;
import com.zote.notification.service.domain.model.UserNotificationPreferences;
import com.zote.notification.service.domain.ports.inbound.ManagePreferencesPort;
import com.zote.notification.service.domain.ports.outbound.repository.UserPreferencesRepositoryPort;
import com.zote.notification.service.domain.support.QueryParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManagePreferencesImpl implements ManagePreferencesPort {

    private final UserPreferencesRepositoryPort preferenceRepository;
    private final QueryParser queryParser;

    @Override
    public UserNotificationPreferences updatePreferences(UpdatePreferencesData data) {
        log.info("Updating preferences for user: {}", data.getUserId());

        // Get existing preferences
        List<UserNotificationPreferences> existingPreferences =
            preferenceRepository.findByUserIdIn(List.of(data.getUserId()));

        Map<NotificationChannel, UserNotificationPreferences> preferencesByChannel = new HashMap<>();

        // Update or create preferences for each channel
        if (data.getChannelEnabled() != null) {
            for (Map.Entry<NotificationChannel, Boolean> entry : data.getChannelEnabled().entrySet()) {
                NotificationChannel channel = entry.getKey();
                Boolean enabled = entry.getValue();

                UserNotificationPreferences pref = existingPreferences.stream()
                    .filter(p -> p.getChannel() == channel)
                    .findFirst()
                    .orElse(createDefaultPreferences(data.getUserId(), channel));

                pref.setEnabled(enabled);
                pref.setUpdatedAt(LocalDateTime.now());

                preferencesByChannel.put(channel, pref);
            }
        }

        // Parse time strings
        data.setParsedQuietHoursStart(queryParser.parseTime(data.getQuietHoursStart()));
        data.setParsedQuietHoursEnd(queryParser.parseTime(data.getQuietHoursEnd()));

        // Update common fields for all preferences
        for (UserNotificationPreferences pref : preferencesByChannel.values()) {
            if (data.getLocale() != null) {
                pref.setLocale(data.getLocale());
            }
            if (data.getTimezone() != null) {
                pref.setTimezone(data.getTimezone());
            }
            if (data.getParsedQuietHoursStart() != null) {
                pref.setQuietHoursStart(data.getParsedQuietHoursStart());
            }
            if (data.getParsedQuietHoursEnd() != null) {
                pref.setQuietHoursEnd(data.getParsedQuietHoursEnd());
            }
            if (data.getCustomPreferences() != null) {
                pref.setPreferences(data.getCustomPreferences());
            }

            preferenceRepository.save(pref);
        }

        log.info("Preferences updated successfully for user: {}", data.getUserId());

        // Return first preference (or create default if none)
        return preferencesByChannel.values().stream()
            .findFirst()
            .orElse(createDefaultPreferences(data.getUserId(), NotificationChannel.EMAIL));
    }

    @Override
    public UserNotificationPreferences getPreferences(String userId) {
        log.info("Getting preferences for user: {}", userId);

        return preferenceRepository.findByUserId(userId)
            .orElse(createDefaultPreferences(userId, NotificationChannel.EMAIL));
    }

    @Override
    public List<UserNotificationPreferences> getUserChannelPreferences(String userId) {
        log.info("Getting all channel preferences for user: {}", userId);

        List<UserNotificationPreferences> preferences =
            preferenceRepository.findByUserIdIn(List.of(userId));

        // Ensure we have preferences for all channels
        for (NotificationChannel channel : NotificationChannel.values()) {
            boolean hasChannel = preferences.stream()
                .anyMatch(p -> p.getChannel() == channel);

            if (!hasChannel) {
                UserNotificationPreferences defaultPref =
                    createDefaultPreferences(userId, channel);
                defaultPref = preferenceRepository.save(defaultPref);
                preferences.add(defaultPref);
            }
        }

        return preferences;
    }

    @Override
    public void enableChannel(String userId, NotificationChannel channel) {
        log.info("Enabling channel {} for user: {}", channel, userId);

        UserNotificationPreferences preferences =
            preferenceRepository.findByUserIdAndChannel(userId, channel)
                .orElse(createDefaultPreferences(userId, channel));

        preferences.setEnabled(true);
        preferences.setUpdatedAt(LocalDateTime.now());

        preferenceRepository.save(preferences);

        log.info("Channel {} enabled for user: {}", channel, userId);
    }

    @Override
    public void disableChannel(String userId, NotificationChannel channel) {
        log.info("Disabling channel {} for user: {}", channel, userId);

        UserNotificationPreferences preferences =
            preferenceRepository.findByUserIdAndChannel(userId, channel)
                .orElse(createDefaultPreferences(userId, channel));

        preferences.setEnabled(false);
        preferences.setUpdatedAt(LocalDateTime.now());

        preferenceRepository.save(preferences);

        log.info("Channel {} disabled for user: {}", channel, userId);
    }

    @Override
    public void setLocale(String userId, String locale) {
        log.info("Setting locale to {} for user: {}", locale, userId);

        // Update locale for all channel preferences
        List<UserNotificationPreferences> allPreferences =
            getUserChannelPreferences(userId);

        for (UserNotificationPreferences pref : allPreferences) {
            pref.setLocale(locale);
            pref.setUpdatedAt(LocalDateTime.now());
            preferenceRepository.save(pref);
        }

        log.info("Locale set to {} for user: {}", locale, userId);
    }

    @Override
    public void setQuietHours(String userId, String startTime, String endTime) {
        log.info("Setting quiet hours {} - {} for user: {}", startTime, endTime, userId);

        var start = queryParser.parseTime(startTime);
        var end = queryParser.parseTime(endTime);

        var allPreferences = getUserChannelPreferences(userId);

        for (var pref : allPreferences) {
            pref.setQuietHoursStart(start);
            pref.setQuietHoursEnd(end);
            pref.setUpdatedAt(LocalDateTime.now());
            preferenceRepository.save(pref);
        }

        log.info("Quiet hours set to {} - {} for user: {}", start, end, userId);
    }

    @Override
    public void setTimezone(String userId, String timezone) {
        log.info("Setting timezone to {} for user: {}", timezone, userId);

        // Update timezone for all channel preferences
        List<UserNotificationPreferences> allPreferences =
            getUserChannelPreferences(userId);

        for (UserNotificationPreferences pref : allPreferences) {
            pref.setTimezone(timezone);
            pref.setUpdatedAt(LocalDateTime.now());
            preferenceRepository.save(pref);
        }

        log.info("Timezone set to {} for user: {}", timezone, userId);
    }

    private UserNotificationPreferences createDefaultPreferences(String userId, NotificationChannel channel) {
        return UserNotificationPreferences.builder()
            .id(generatePreferenceId())
            .userId(userId)
            .channel(channel)
            .enabled(true) // Channels are enabled by default
            .locale("en") // Default locale
            .timezone("UTC") // Default timezone
            .quietHoursStart(LocalTime.of(22, 0)) // 10 PM
            .quietHoursEnd(LocalTime.of(7, 0)) // 7 AM
            .preferences(new HashMap<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private String generatePreferenceId() {
        return "pref-" + UUID.randomUUID().toString();
    }
}
