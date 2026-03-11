package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.ComplianceViolationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceViolationRepository extends JpaRepository<ComplianceViolationEntity, String> {

    Page<ComplianceViolationEntity> findByPolicyIdOrderByDetectedAtDesc(String policyId, Pageable pageable);

    Page<ComplianceViolationEntity> findByStatusOrderByDetectedAtDesc(String status, Pageable pageable);

    List<ComplianceViolationEntity> findByPolicyIdAndStatus(String policyId, String status);
}
