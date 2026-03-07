package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyStatusHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PolicyStatusHistoryRepositoryPort {

    PolicyStatusHistory saveHistory(PolicyStatusHistory history);

    Page<PolicyStatusHistory> findAllByPolicyId(String policyId, Pageable pageable);

    PolicyStatusHistory findLatestByPolicyId(String policyId);
}
