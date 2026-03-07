package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyLifeDetails;
import com.zote.policy.service.domain.ports.outbound.PolicyLifeDetailsRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyLifeDetailsEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyLifeDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyLifeDetailsRepositoryPortImpl implements PolicyLifeDetailsRepositoryPort {

    private final PolicyLifeDetailsRepository repository;

    @Override
    public PolicyLifeDetails save(PolicyLifeDetails details) {
        return repository.save(PolicyLifeDetailsEntity.toEntity(details)).toDto();
    }

    @Override
    public PolicyLifeDetails findByPolicyVersionId(String policyVersionId) {
        return repository.findByPolicyVersionId(policyVersionId)
                .map(PolicyLifeDetailsEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Life details not found for policyVersionId " + policyVersionId));
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
