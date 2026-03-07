package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyLifeBeneficiary;

import java.util.List;

public interface PolicyLifeBeneficiaryRepositoryPort {

    PolicyLifeBeneficiary save(PolicyLifeBeneficiary beneficiary);

    List<PolicyLifeBeneficiary> saveAll(List<PolicyLifeBeneficiary> beneficiaries);

    List<PolicyLifeBeneficiary> findByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);

    long countByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionIdAndName(String policyVersionId, String name);
}
