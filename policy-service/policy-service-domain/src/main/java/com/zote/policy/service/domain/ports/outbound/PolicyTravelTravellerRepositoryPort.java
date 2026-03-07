package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyTravelTraveller;

import java.util.List;

public interface PolicyTravelTravellerRepositoryPort {

    PolicyTravelTraveller save(PolicyTravelTraveller traveller);

    List<PolicyTravelTraveller> saveAll(List<PolicyTravelTraveller> travellers);

    List<PolicyTravelTraveller> findByPolicyVersionId(String policyVersionId);

    void deleteByPolicyVersionId(String policyVersionId);

    long countByPolicyVersionId(String policyVersionId);

    boolean existsByPolicyVersionIdAndName(String policyVersionId, String name);
}
