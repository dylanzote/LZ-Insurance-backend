package com.zote.policy.service.infrastructure.scheduler;

import com.zote.policy.service.infrastructure.outbound.entities.AutomationLogEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.AutomationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Persists automation job results for audit trail (13.6).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AutomationAuditService {

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PARTIAL = "PARTIAL";

    public static final String JOB_RENEWAL_GENERATION = "RENEWAL_GENERATION";
    public static final String JOB_NON_PAYMENT_CANCELLATION = "NON_PAYMENT_CANCELLATION";
    public static final String JOB_OVERDUE_DETECTION = "OVERDUE_DETECTION";

    private final AutomationLogRepository repository;

    @Transactional
    public AutomationLogEntity startJob(String jobType, Map<String, Object> metadata) {
        var entity = AutomationLogEntity.builder()
                .id(UUID.randomUUID().toString())
                .jobType(jobType)
                .status("RUNNING")
                .itemsProcessed(0)
                .itemsSucceeded(0)
                .itemsFailed(0)
                .startedAt(LocalDateTime.now())
                .metadata(metadata)
                .build();
        return repository.save(entity);
    }

    @Transactional
    public void completeJob(String logId, int processed, int succeeded, int failed, String errorMessage) {
        repository.findById(logId).ifPresent(entity -> {
            entity.setItemsProcessed(processed);
            entity.setItemsSucceeded(succeeded);
            entity.setItemsFailed(failed);
            entity.setFinishedAt(LocalDateTime.now());
            entity.setErrorMessage(errorMessage);
            entity.setStatus(determineStatus(failed, errorMessage));
            repository.save(entity);
        });
    }

    private String determineStatus(int failed, String errorMessage) {
        if (errorMessage != null && !errorMessage.isBlank()) {
            return STATUS_FAILED;
        }
        if (failed > 0) {
            return STATUS_PARTIAL;
        }
        return STATUS_SUCCESS;
    }
}
