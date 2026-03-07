package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyCoverageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyCoverageRepository extends JpaRepository<PolicyCoverageEntity, String> {

    List<PolicyCoverageEntity> findAllByPolicyVersionId(String policyVersionId);

    Optional<PolicyCoverageEntity> findByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode);

    boolean existsByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode);

    void deleteAllByPolicyVersionId(String policyVersionId);
}
