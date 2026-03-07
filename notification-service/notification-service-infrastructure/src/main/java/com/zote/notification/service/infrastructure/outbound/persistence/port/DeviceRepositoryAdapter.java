package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.UserDevice;
import com.zote.notification.service.domain.ports.outbound.repository.DeviceRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.UserDeviceEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceRepositoryAdapter implements DeviceRepositoryPort {

    private final DeviceRepository deviceRepository;

    @Override
    public UserDevice save(UserDevice device) {
        log.info("saving device: {}", device);
        return deviceRepository.save(UserDeviceEntity.toEntity(device)).toDto();
    }

    @Override
    public UserDevice findById(String id) {
        log.info("finding device with id: {}", id);
        return deviceRepository.findById(id)
                .map(UserDeviceEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find device with id " + id));
    }

    @Override
    public UserDevice findByDeviceId(String deviceId) {
        log.info("finding device with deviceId: {}", deviceId);
        return deviceRepository.findByDeviceId(deviceId)
                .map(UserDeviceEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find device with deviceId " + deviceId));
    }

    @Override
    public Optional<UserDevice> findByUserIdAndDeviceId(String userId, String deviceId) {
        log.info("finding device with userId: {} and deviceId: {}", userId, deviceId);
        return deviceRepository.findByUserIdAndDeviceId(userId, deviceId)
                .map(UserDeviceEntity::toDto);
    }

    @Override
    public List<UserDevice> findByUserId(String userId) {
        log.info("finding devices for userId: {}", userId);
        return deviceRepository.findByUserId(userId).stream()
                .map(UserDeviceEntity::toDto)
                .toList();
    }

    @Override
    public List<UserDevice> findActiveDevicesByUserId(String userId) {
        log.info("finding active devices for userId: {}", userId);
        return deviceRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(UserDeviceEntity::toDto)
                .toList();
    }

    @Override
    public UserDevice findByPushToken(String pushToken) {
        log.info("finding device with pushToken: {}", pushToken);
        return deviceRepository.findByPushToken(pushToken)
                .map(UserDeviceEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find device with pushToken " + pushToken));
    }

    @Override
    public void deleteById(String id) {
        log.info("deleting device with id: {}", id);
        deviceRepository.deleteById(id);
    }

    @Override
    public void deactivateToken(String pushToken) {
        log.info("deactivating device with pushToken: {}", pushToken);
        deviceRepository.findByPushToken(pushToken).ifPresent(deviceEntity -> {
            deviceEntity.setIsActive(false);
            deviceRepository.save(deviceEntity);
        });
    }

    @Override
    public void deleteByUserId(String userId) {
        log.info("deleting devices for userId: {}", userId);
        deviceRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByDeviceId(String deviceId) {
        log.info("deleting device with deviceId: {}", deviceId);
        deviceRepository.deleteByDeviceId(deviceId);
    }

    @Override
    public void updateLastSeen(String deviceId, LocalDateTime lastSeenAt) {
        log.info("updating last seen for deviceId: {} to {}", deviceId, lastSeenAt);
        deviceRepository.findById(deviceId).ifPresent(deviceEntity -> {
            deviceEntity.setLastSeenAt(lastSeenAt);
            deviceRepository.save(deviceEntity);
        });
    }

    @Override
    public void addTopicSubscription(String deviceId, String topic) {
        log.info("adding topic subscription: {} for deviceId: {}", topic, deviceId);
        deviceRepository.findById(deviceId).ifPresent(deviceEntity -> {
            Set<String> topics = deviceEntity.getSubscribedTopics();
            if (topics == null) {
                topics = new HashSet<>();
            }
            topics.add(topic);
            deviceEntity.setSubscribedTopics(topics);
            deviceRepository.save(deviceEntity);
        });
    }

    @Override
    public void removeTopicSubscription(String deviceId, String topic) {
        log.info("removing topic subscription: {} for deviceId: {}", topic, deviceId);
        deviceRepository.findById(deviceId).ifPresent(deviceEntity -> {
            Set<String> topics = deviceEntity.getSubscribedTopics();
            if (topics != null) {
                topics.remove(topic);
                deviceEntity.setSubscribedTopics(topics);
                deviceRepository.save(deviceEntity);
            }
        });
    }

    @Override
    public List<String> getDeviceTopics(String deviceId) {
        log.info("getting topics for deviceId: {}", deviceId);
        return deviceRepository.findById(deviceId)
                .map(UserDeviceEntity::getSubscribedTopics)
                .map(List::copyOf)
                .orElseThrow(() -> new FunctionalError("could not find device with id " + deviceId));
    }
}
