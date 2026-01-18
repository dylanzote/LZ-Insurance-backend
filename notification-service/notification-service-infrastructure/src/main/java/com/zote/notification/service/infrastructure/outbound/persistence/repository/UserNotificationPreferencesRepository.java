package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.infrastructure.outbound.entities.UserNotificationPreferencesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationPreferencesRepository extends JpaRepository<UserNotificationPreferencesEntity, String> {
    Optional<UserNotificationPreferencesEntity> findByUserId(String userId);

    Optional<UserNotificationPreferencesEntity> findByUserIdAndChannel(String userId, NotificationChannel channel);

    List<UserNotificationPreferencesEntity> findByUserIdIn(List<String> userIds);
}
