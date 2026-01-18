package com.zote.notification.service.domain.ports.outbound.repository;

import com.zote.notification.service.domain.model.DeadLetterQueueItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DeadLetterQueueRepositoryPort {
    DeadLetterQueueItem save(DeadLetterQueueItem item);
    List<DeadLetterQueueItem> findByProcessedFalseAndScheduledRetryAtBefore(LocalDateTime time);
    DeadLetterQueueItem findById(Long id);
    long countByProcessedFalse();

    List<DeadLetterQueueItem> findByProcessedFalse();
    Page<DeadLetterQueueItem> findAll(Pageable pageable);
    void deleteById(Long id);
    void deleteByNotificationId(String notificationId);
}
