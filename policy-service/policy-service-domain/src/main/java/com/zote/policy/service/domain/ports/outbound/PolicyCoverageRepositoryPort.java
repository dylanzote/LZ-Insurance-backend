package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyCoverage;

import java.util.List;

public interface PolicyCoverageRepositoryPort {

    PolicyCoverage savePolicyCoverage(PolicyCoverage coverage);

    PolicyCoverage findById(String id);

    void deleteById(String id);

    List<PolicyCoverage> findAllByPolicyVersionId(String policyVersionId);

    PolicyCoverage findByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode);

    boolean existsByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode);

    void deleteAllByPolicyVersionId(String policyVersionId);
}
