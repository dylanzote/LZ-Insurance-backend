package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyHomeDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyHomeDetailsRepository extends JpaRepository<PolicyHomeDetailsEntity, String> {

    Optional<PolicyHomeDetailsEntity> findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
