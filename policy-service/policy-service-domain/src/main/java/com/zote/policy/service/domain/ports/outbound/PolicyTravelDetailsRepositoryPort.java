package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyTravelDetails;

public interface PolicyTravelDetailsRepositoryPort {

    PolicyTravelDetails save(PolicyTravelDetails details);

    PolicyTravelDetails findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
