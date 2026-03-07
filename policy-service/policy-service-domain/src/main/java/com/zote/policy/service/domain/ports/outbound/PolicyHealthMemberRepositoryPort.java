package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyHealthMember;

import java.util.List;

public interface PolicyHealthMemberRepositoryPort {

    PolicyHealthMember save(PolicyHealthMember member);

    List<PolicyHealthMember> saveAll(List<PolicyHealthMember> members);

    List<PolicyHealthMember> findByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);

    long countByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionIdAndName(String policyVersionId, String name);
}
