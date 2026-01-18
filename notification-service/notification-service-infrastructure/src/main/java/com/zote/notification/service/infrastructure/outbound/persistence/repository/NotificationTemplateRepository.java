package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.infrastructure.outbound.entities.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, String> {

    Optional<NotificationTemplateEntity> findByName(String name);

    Optional<NotificationTemplateEntity> findByChannel(NotificationChannel channel);

    List<NotificationTemplateEntity> findByChannelAndIsActive(NotificationChannel channel, Boolean isActive);

}
