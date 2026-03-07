package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyHomeDetails;

public interface PolicyHomeDetailsRepositoryPort {

    PolicyHomeDetails save(PolicyHomeDetails details);

    PolicyHomeDetails findByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);
}
