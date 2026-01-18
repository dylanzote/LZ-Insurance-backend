package com.zote.notification.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.DeadLetterQueueItem;
import com.zote.notification.service.domain.ports.outbound.repository.DeadLetterQueueRepositoryPort;
import com.zote.notification.service.infrastructure.outbound.entities.DeadLetterQueueEntity;
import com.zote.notification.service.infrastructure.outbound.persistence.repository.DeadLetterQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueRepositoryAdapter implements DeadLetterQueueRepositoryPort {

    private final DeadLetterQueueRepository deadLetterQueueRepository;

    @Override
    public DeadLetterQueueItem save(DeadLetterQueueItem item) {
        log.info("saving dead letter queue item: {}", item);
        return deadLetterQueueRepository.save(DeadLetterQueueEntity.toEntity(item)).toDto();
    }

    @Override
    public List<DeadLetterQueueItem> findByProcessedFalseAndScheduledRetryAtBefore(LocalDateTime time) {
        log.info("finding dead letter queue items with processed false and scheduledRetryAt before: {}", time);
        return deadLetterQueueRepository.findByProcessedFalseAndScheduledRetryAtBefore(time).stream()
                .map(DeadLetterQueueEntity::toDto)
                .toList();
    }

    @Override
    public DeadLetterQueueItem findById(Long id) {
        log.info("finding dead letter queue item with id: {}", id);
        return deadLetterQueueRepository.findById(id)
                .map(DeadLetterQueueEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find dead letter queue item with id " + id));
    }

    @Override
    public long countByProcessedFalse() {
        log.info("counting dead letter queue items with processed false");
        return deadLetterQueueRepository.countByProcessedFalse();
    }

    @Override
    public List<DeadLetterQueueItem> findByProcessedFalse() {
        log.info("finding dead letter queue items with processed false");
        return deadLetterQueueRepository.findByProcessedFalse().stream()
                .map(DeadLetterQueueEntity::toDto)
                .toList();
    }

    @Override
    public Page<DeadLetterQueueItem> findAll(Pageable pageable) {
        log.info("finding all dead letter queue items with pageable: {}", pageable);
        return deadLetterQueueRepository.findAll(pageable)
                .map(DeadLetterQueueEntity::toDto);
    }

    @Override
    public void deleteById(Long id) {
        log.info("deleting dead letter queue item with id: {}", id);
        deadLetterQueueRepository.deleteById(id);
    }

    @Override
    public void deleteByNotificationId(String notificationId) {
        log.info("deleting dead letter queue items with notificationId: {}", notificationId);
        deadLetterQueueRepository.deleteByNotificationId(notificationId);
    }
}
