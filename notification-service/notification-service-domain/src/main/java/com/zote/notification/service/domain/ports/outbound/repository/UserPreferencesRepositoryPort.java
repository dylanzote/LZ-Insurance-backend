package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.UserNotificationPreferences;

import java.util.List;

import java.util.Optional;

public interface UserPreferencesRepositoryPort {

    UserNotificationPreferences save(UserNotificationPreferences preferences);
    Optional<UserNotificationPreferences> findByUserId(String userId);
    Optional<UserNotificationPreferences> findByUserIdAndChannel(String userId, NotificationChannel channel);
    List<UserNotificationPreferences> findByUserIdIn(List<String> userIds);
}
