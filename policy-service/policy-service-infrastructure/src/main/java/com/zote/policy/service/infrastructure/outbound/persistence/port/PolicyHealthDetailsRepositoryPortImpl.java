package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyHealthDetails;
import com.zote.policy.service.domain.ports.outbound.PolicyHealthDetailsRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyHealthDetailsEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyHealthDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyHealthDetailsRepositoryPortImpl implements PolicyHealthDetailsRepositoryPort {

    private final PolicyHealthDetailsRepository repository;

    @Override
    public PolicyHealthDetails save(PolicyHealthDetails details) {
        return repository.save(PolicyHealthDetailsEntity.toEntity(details)).toDto();
    }

    @Override
    public PolicyHealthDetails findByPolicyVersionId(String policyVersionId) {
        return repository.findByPolicyVersionId(policyVersionId)
                .map(PolicyHealthDetailsEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Health details not found for policyVersionId " + policyVersionId));
    }

    @Override
    public boolean existsByPolicyVersionId(String policyVersionId) {
        return repository.existsByPolicyVersionId(policyVersionId);
    }

    @Override
    public void deleteByPolicyVersionId(String policyVersionId) {
        repository.deleteByPolicyVersionId(policyVersionId);
    }
}
