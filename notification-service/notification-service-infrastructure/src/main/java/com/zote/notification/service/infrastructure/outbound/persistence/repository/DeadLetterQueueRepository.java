package com.zote.notification.service.infrastructure.outbound.persistence.repository;

import com.zote.notification.service.infrastructure.outbound.entities.DeadLetterQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeadLetterQueueRepository extends JpaRepository<DeadLetterQueueEntity, Long> {

    List<DeadLetterQueueEntity> findByProcessedFalseAndScheduledRetryAtBefore(java.time.LocalDateTime time);

    long countByProcessedFalse();

    List<DeadLetterQueueEntity> findByProcessedFalse();

    void deleteByNotificationId(String notificationId);
}
