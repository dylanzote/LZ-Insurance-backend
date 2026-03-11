package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyAuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyAuditLogRepository extends JpaRepository<PolicyAuditLogEntity, String> {

    Page<PolicyAuditLogEntity> findByPolicyIdOrderByChangedAtDesc(String policyId, Pageable pageable);

    Page<PolicyAuditLogEntity> findByPolicyIdAndChangeTypeOrderByChangedAtDesc(
            String policyId, String changeType, Pageable pageable);
}
