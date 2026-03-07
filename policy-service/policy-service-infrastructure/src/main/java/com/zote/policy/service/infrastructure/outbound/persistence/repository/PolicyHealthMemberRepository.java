package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyHealthMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyHealthMemberRepository extends JpaRepository<PolicyHealthMemberEntity, String> {

    List<PolicyHealthMemberEntity> findAllByPolicyVersion_Id(String policyVersionId);

    void deleteAllByPolicyVersion_Id(String policyVersionId);

    long countByPolicyVersion_Id(String policyVersionId);

    boolean existsByPolicyVersion_IdAndNameIgnoreCase(String policyVersionId, String name);
}
