package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyHealthDetails;

public interface PolicyHealthDetailsRepositoryPort {

    PolicyHealthDetails save(PolicyHealthDetails details);

    PolicyHealthDetails findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
