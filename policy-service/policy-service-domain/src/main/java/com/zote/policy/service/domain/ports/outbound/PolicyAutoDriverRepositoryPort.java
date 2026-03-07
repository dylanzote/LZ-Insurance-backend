package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyAutoDriver;

import java.util.List;

public interface PolicyAutoDriverRepositoryPort {

    PolicyAutoDriver save(PolicyAutoDriver driver);

    List<PolicyAutoDriver> saveAll(List<PolicyAutoDriver> drivers);

    List<PolicyAutoDriver> findByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);

    long countByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionIdAndName(String policyVersionId, String name);
}
