package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.PolicyHealthMember;
import com.zote.policy.service.domain.ports.outbound.PolicyHealthMemberRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyHealthMemberEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyHealthMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PolicyHealthMemberRepositoryPortImpl implements PolicyHealthMemberRepositoryPort {

    private final PolicyHealthMemberRepository repository;
    @Override
    public PolicyHealthMember save(PolicyHealthMember member) {
        return repository.save(PolicyHealthMemberEntity.toEntity(member)).toDto();
    }

    @Override
    public List<PolicyHealthMember> saveAll(List<PolicyHealthMember> members) {
        return repository.saveAll(members.stream().map(PolicyHealthMemberEntity::toEntity).toList())
                .stream()
                .map(PolicyHealthMemberEntity::toDto)
                .toList();
    }

    @Override
    public List<PolicyHealthMember> findByPolicyVersionId(String policyVersionId) {
        return repository.findAllByPolicyVersion_Id(policyVersionId)
                .stream().map(PolicyHealthMemberEntity::toDto).toList();
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
