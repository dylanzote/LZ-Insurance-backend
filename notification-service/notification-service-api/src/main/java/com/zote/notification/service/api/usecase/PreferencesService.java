package com.zote.notification.service.api.usecase;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.api.controller.PreferencesApi;
import com.zote.notification.service.api.request.UpdatePreferencesRequest;
import com.zote.notification.service.api.response.UserPreferencesResponse;
import com.zote.notification.service.domain.ports.inbound.ManagePreferencesPort;
import com.zote.notification.service.domain.usecases.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PreferencesService implements PreferencesApi {

    private final ManagePreferencesPort managePreferencesPort;
    private final UserValidationService userValidationService;

    @Override
    public UserPreferencesResponse getPreferences(String userId) {
        log.info("API: Getting preferences for user: {}", userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        var preferences = managePreferencesPort.getPreferences(userId);
        return UserPreferencesResponse.fromDomain(preferences);
    }

    @Override
    public UserPreferencesResponse updatePreferences(String userId, UpdatePreferencesRequest request) {
        log.info("API: Updating preferences for user: {}", userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        var data = request.toUpdatePreferencesData(userId);
        var preferences = managePreferencesPort.updatePreferences(data);
        return UserPreferencesResponse.fromDomain(preferences);
    }

    @Override
    public void enableChannel(String userId, String channel) {
        log.info("API: Enabling channel {} for user: {}", channel, userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        var notificationChannel = NotificationChannel.valueOf(channel.toUpperCase());
        managePreferencesPort.enableChannel(userId, notificationChannel);
    }

    @Override
    public void disableChannel(String userId, String channel) {
        log.info("API: Disabling channel {} for user: {}", channel, userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        var notificationChannel = NotificationChannel.valueOf(channel.toUpperCase());
        managePreferencesPort.disableChannel(userId, notificationChannel);
    }

    @Override
    public void setLocale(String userId, String locale) {
        log.info("API: Setting locale to {} for user: {}", locale, userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        managePreferencesPort.setLocale(userId, locale);
    }

    @Override
    public void setQuietHours(String userId, String startTime, String endTime) {
        log.info("API: Setting quiet hours {} - {} for user: {}", startTime, endTime, userId);
        
        // Validate user exists
        userValidationService.validateAndGetUser(userId);
        
        managePreferencesPort.setQuietHours(userId, startTime, endTime);
    }
}
