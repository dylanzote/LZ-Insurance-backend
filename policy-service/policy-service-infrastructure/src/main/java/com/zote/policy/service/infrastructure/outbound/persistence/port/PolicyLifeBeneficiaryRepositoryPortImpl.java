package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.PolicyLifeBeneficiary;
import com.zote.policy.service.domain.ports.outbound.PolicyLifeBeneficiaryRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyLifeBeneficiaryEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyLifeBeneficiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyLifeBeneficiaryRepositoryPortImpl implements PolicyLifeBeneficiaryRepositoryPort {

    private final PolicyLifeBeneficiaryRepository repository;

    @Override
    public PolicyLifeBeneficiary save(PolicyLifeBeneficiary beneficiary) {
        return repository.save(PolicyLifeBeneficiaryEntity.toEntity(beneficiary)).toDto();
    }

    @Override
    public List<PolicyLifeBeneficiary> saveAll(List<PolicyLifeBeneficiary> beneficiaries) {
        return repository.saveAll(beneficiaries.stream()
                .map(PolicyLifeBeneficiaryEntity::toEntity)
                .toList())
                .stream()
                .map(PolicyLifeBeneficiaryEntity::toDto)
                .toList();
    }

    @Override
    public List<PolicyLifeBeneficiary> findByPolicyVersionId(String policyVersionId) {
        return repository.findAllByPolicyVersion_Id(policyVersionId)
                .stream().map(PolicyLifeBeneficiaryEntity::toDto).toList();
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
