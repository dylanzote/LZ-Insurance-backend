package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.UserNotificationPreferences;
import com.zote.notification.service.domain.ports.outbound.repository.UserPreferencesRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.UserNotificationPreferencesEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.UserNotificationPreferencesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserNotificationPreferencesAdapter implements UserPreferencesRepositoryPort {

    private final UserNotificationPreferencesRepository preferencesRepository;

    @Override
    public UserNotificationPreferences save(UserNotificationPreferences preferences) {
        log.info("saving user notification preferences: {}", preferences);
        return preferencesRepository.save(UserNotificationPreferencesEntity.toEntity(preferences)).toDto();
    }

    @Override
    public Optional<UserNotificationPreferences> findByUserId(String userId) {
        log.info("finding user notification preferences for userId: {}", userId);
        return preferencesRepository.findByUserId(userId)
                .map(UserNotificationPreferencesEntity::toDto);
    }

    @Override
    public Optional<UserNotificationPreferences> findByUserIdAndChannel(String userId, NotificationChannel channel) {
        log.info("finding user notification preferences for userId: {} and channel: {}", userId, channel);
        return preferencesRepository.findByUserIdAndChannel(userId, channel)
                .map(UserNotificationPreferencesEntity::toDto);
    }

    @Override
    public List<UserNotificationPreferences> findByUserIdIn(List<String> userIds) {
        log.info("finding user notification preferences for userIds: {}", userIds);
        return preferencesRepository.findByUserIdIn(userIds).stream()
                .map(UserNotificationPreferencesEntity::toDto)
                .toList();
    }
}
