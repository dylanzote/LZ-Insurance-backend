package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyTravelDetails;
import com.zote.policy.service.domain.ports.outbound.PolicyTravelDetailsRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyTravelDetailsEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyTravelDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyTravelDetailsRepositoryPortImpl implements PolicyTravelDetailsRepositoryPort {

    private final PolicyTravelDetailsRepository repository;

    @Override
    public PolicyTravelDetails save(PolicyTravelDetails details) {
        return repository.save(PolicyTravelDetailsEntity.toEntity(details)).toDto();
    }

    @Override
    public PolicyTravelDetails findByPolicyVersionId(String policyVersionId) {
        return repository.findByPolicyVersionId(policyVersionId)
                .map(PolicyTravelDetailsEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Travel details not found for policyVersionId " + policyVersionId));
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
