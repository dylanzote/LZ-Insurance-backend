package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.AutomationLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AutomationLogRepository extends JpaRepository<AutomationLogEntity, String> {

    Page<AutomationLogEntity> findByJobTypeOrderByStartedAtDesc(String jobType, Pageable pageable);

    List<AutomationLogEntity> findByJobTypeAndStartedAtBetweenOrderByStartedAtDesc(
            String jobType, LocalDateTime from, LocalDateTime to);
}
