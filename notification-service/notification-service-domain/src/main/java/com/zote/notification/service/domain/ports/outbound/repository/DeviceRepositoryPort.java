package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.notification.service.domain.model.UserDevice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceRepositoryPort {
    UserDevice save(UserDevice device);
    UserDevice findById(String id);

    UserDevice findByDeviceId(String deviceId);
    Optional<UserDevice> findByUserIdAndDeviceId(String userId, String deviceId);
    List<UserDevice> findByUserId(String userId);
    List<UserDevice> findActiveDevicesByUserId(String userId);
    UserDevice findByPushToken(String pushToken);
    void deleteById(String id);
    void deactivateToken(String pushToken);
    void deleteByUserId(String userId);
    void updateLastSeen(String deviceId, LocalDateTime lastSeenAt);

    // Topic management
    void addTopicSubscription(String deviceId, String topic);
    void removeTopicSubscription(String deviceId, String topic);
    List<String> getDeviceTopics(String deviceId);
}
