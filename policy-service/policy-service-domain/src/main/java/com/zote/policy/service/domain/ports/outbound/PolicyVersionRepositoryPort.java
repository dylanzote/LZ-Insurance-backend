package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyVersion;

import java.time.LocalDate;
import java.util.List;

public interface PolicyVersionRepositoryPort {

    PolicyVersion savePolicyVersion(PolicyVersion version);

    List<PolicyVersion> findAllByPolicyIdOrderByVersionNoDesc(String policyId);

    PolicyVersion findByPolicyIdAndVersionNo(String policyId, int versionNo);

    PolicyVersion findCurrentVersion(String policyId);

    int findMaxVersionNo(String policyId);

    boolean existsByPolicyIdAndVersionNo(String policyId, int versionNo);

    void closeVersion(String versionId, LocalDate effectiveTo);
}
