package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationProviderRepository extends JpaRepository<NotificationProviderEntity, String> {

    Optional<NotificationProviderEntity> findFirstByChannelAndIsActiveOrderByPriorityAsc(NotificationChannel channel, Boolean isActive);

    List<NotificationProviderEntity> findByChannel(NotificationChannel channel);

    List<NotificationProviderEntity> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive);

    List<NotificationProviderEntity> findByChannelOrderByPriority(NotificationChannel channel);

    Optional<NotificationProviderEntity> findByName(String name);

}
