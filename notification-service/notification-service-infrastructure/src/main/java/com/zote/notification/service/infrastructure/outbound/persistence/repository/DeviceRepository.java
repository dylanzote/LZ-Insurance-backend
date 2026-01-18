package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.notification.service.infrastructure.outbound.entities.UserDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<UserDeviceEntity, String> {

    Optional<UserDeviceEntity> findByUserIdAndDeviceId(String userId, String deviceId);

    List<UserDeviceEntity> findByUserId(String userId);

    List<UserDeviceEntity> findByUserIdAndIsActiveTrue(String userId);

    Optional<UserDeviceEntity> findByPushToken(String pushToken);

    void deleteByUserIdAndDeviceId(String userId, String deviceId);

    void deleteByUserId(String userId);

    Optional<UserDeviceEntity> findByDeviceId(String deviceId);

    @Query("SELECT d.pushToken FROM UserDeviceEntity d WHERE d.userId = :userId AND d.isActive = true AND d.pushToken IS NOT NULL")
    List<String> findActivePushTokensByUserId(@Param("userId") String userId);

    @Query("SELECT d FROM UserDeviceEntity d WHERE d.userId = :userId AND d.isActive = true AND d.isExpo = true")
    List<UserDeviceEntity> findActiveExpoDevicesByUserId(@Param("userId") String userId);

}
