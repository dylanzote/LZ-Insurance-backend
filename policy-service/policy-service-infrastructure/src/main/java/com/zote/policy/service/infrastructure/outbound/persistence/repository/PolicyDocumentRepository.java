package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyDocumentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyDocumentRepository extends JpaRepository<PolicyDocumentEntity, String> {

    Page<PolicyDocumentEntity> findAllByPolicyId(String policyId, Pageable pageable);

    List<PolicyDocumentEntity> findAllByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status);

    Optional<PolicyDocumentEntity> findByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name);

    boolean existsByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name);

    long countByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status);
}
