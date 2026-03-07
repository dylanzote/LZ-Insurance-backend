package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyStatusHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyStatusHistoryRepository extends JpaRepository<PolicyStatusHistoryEntity, String> {
    Page<PolicyStatusHistoryEntity> findAllByPolicyId(String policyId, Pageable pageable);

    Optional<PolicyStatusHistoryEntity> findTopByPolicyIdOrderByCreatedAtDesc(String policyId);
}
