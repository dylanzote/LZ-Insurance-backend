package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.PolicyAutoDriver;
import com.zote.policy.service.domain.ports.outbound.PolicyAutoDriverRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyAutoDriverEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyAutoDriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PolicyAutoDriverRepositoryPortImpl implements PolicyAutoDriverRepositoryPort {

    private final PolicyAutoDriverRepository repository;
    @Override
    public PolicyAutoDriver save(PolicyAutoDriver driver) {
        return repository.save(PolicyAutoDriverEntity.toEntity(driver)).toDto();
    }

    @Override
    public List<PolicyAutoDriver> saveAll(List<PolicyAutoDriver> drivers) {
        return repository.saveAll(drivers.stream().map(PolicyAutoDriverEntity::toEntity).toList())
                    .stream()
                    .map(PolicyAutoDriverEntity::toDto)
                    .toList();
    }

    @Override
    public List<PolicyAutoDriver> findByPolicyVersionId(String policyVersionId) {
        return repository.findAllByPolicyVersion_Id(policyVersionId)
                .stream().map(PolicyAutoDriverEntity::toDto).toList();
    }

    @Override
    public void deleteByPolicyVersionId(String policyVersionId) {
        repository.deleteAllByPolicyVersion_Id(policyVersionId);
    }

    @Override
    public long countByPolicyVersionId(String policyVersionId) {
        return repository.countByPolicyVersion_Id(policyVersionId);
    }

    @Override
    public boolean existsByPolicyVersionIdAndName(String policyVersionId, String name) {
        return repository.existsByPolicyVersion_IdAndNameIgnoreCase(policyVersionId, name);
    }
}
