package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyStatusHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PolicyStatusHistoryRepository extends JpaRepository<PolicyStatusHistoryEntity, String> {
    Page<PolicyStatusHistoryEntity> findAllByPolicyId(String policyId, Pageable pageable);

    Optional<PolicyStatusHistoryEntity> findTopByPolicyIdOrderByCreatedAtDesc(String policyId);

    /** Count cancellations in period (changed_at is mapped from createdAt). */
    @Query("""
        select count(h) from PolicyStatusHistoryEntity h
        where h.toStatus = :status
        and h.createdAt >= :from and h.createdAt < :to
    """)
    long countByToStatusAndCreatedAtBetween(@Param("status") PolicyStatus status,
                                            @Param("from") LocalDateTime from,
                                            @Param("to") LocalDateTime to);
}
