package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyLifeDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyLifeDetailsRepository extends JpaRepository<PolicyLifeDetailsEntity, String> {

    Optional<PolicyLifeDetailsEntity> findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
