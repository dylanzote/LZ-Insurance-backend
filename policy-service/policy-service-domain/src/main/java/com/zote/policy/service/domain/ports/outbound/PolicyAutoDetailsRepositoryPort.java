package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyAutoDetails;

public interface PolicyAutoDetailsRepositoryPort {

    PolicyAutoDetails save(PolicyAutoDetails details);

    PolicyAutoDetails findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
