package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.PolicyTravelTraveller;
import com.zote.policy.service.domain.ports.outbound.PolicyTravelTravellerRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyTravelTravellerEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyTravelTravellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyTravelTravellerRepositoryPortImpl implements PolicyTravelTravellerRepositoryPort {

    private final PolicyTravelTravellerRepository repository;

    @Override
    public PolicyTravelTraveller save(PolicyTravelTraveller traveller) {
        return repository.save(PolicyTravelTravellerEntity.toEntity(traveller)).toDto();
    }

    @Override
    public List<PolicyTravelTraveller> saveAll(List<PolicyTravelTraveller> travellers) {
        return repository.saveAll(travellers.stream().map(PolicyTravelTravellerEntity::toEntity).toList())
                    .stream()
                    .map(PolicyTravelTravellerEntity::toDto)
                    .toList();
    }

    @Override
    public List<PolicyTravelTraveller> findByPolicyVersionId(String policyVersionId) {
        return repository.findAllByPolicyVersion_Id(policyVersionId)
                .stream().map(PolicyTravelTravellerEntity::toDto).toList();
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
