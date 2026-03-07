package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyLifeDetails;

public interface PolicyLifeDetailsRepositoryPort {

    PolicyLifeDetails save(PolicyLifeDetails details);

    PolicyLifeDetails findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
