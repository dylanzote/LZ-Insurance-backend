package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyHomeDetails;
import com.zote.policy.service.domain.ports.outbound.PolicyHomeDetailsRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyHomeDetailsEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyHomeDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyHomeDetailsRepositoryPortImpl implements PolicyHomeDetailsRepositoryPort {

    private final PolicyHomeDetailsRepository repository;

    @Override
    public PolicyHomeDetails save(PolicyHomeDetails details) {
        return repository.save(PolicyHomeDetailsEntity.toEntity(details)).toDto();
    }

    @Override
    public PolicyHomeDetails findByPolicyVersionId(String policyVersionId) {
        return repository.findByPolicyVersionId(policyVersionId)
                .map(PolicyHomeDetailsEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Home details not found for policyVersionId " + policyVersionId));
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
