package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyAutoDetails;
import com.zote.policy.service.domain.ports.outbound.PolicyAutoDetailsRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyAutoDetailsEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyAutoDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyAutoDetailsRepositoryPortImpl implements PolicyAutoDetailsRepositoryPort {

    private final PolicyAutoDetailsRepository repository;

    @Override
    public PolicyAutoDetails save(PolicyAutoDetails details) {
        return repository.save(PolicyAutoDetailsEntity.toEntity(details)).toDto();
    }

    @Override
    public PolicyAutoDetails findByPolicyVersionId(String policyVersionId) {
        return repository.findByPolicyVersionId(policyVersionId)
                .map(PolicyAutoDetailsEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Auto details not found for policyVersionId " + policyVersionId));
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
