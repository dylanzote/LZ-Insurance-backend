package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyStatusHistory;
import com.zote.policy.service.domain.ports.outbound.PolicyStatusHistoryRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyStatusHistoryEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyStatusHistoryRepositoryPortImpl implements PolicyStatusHistoryRepositoryPort {

    private final PolicyStatusHistoryRepository repository;

    @Override
    public PolicyStatusHistory saveHistory(PolicyStatusHistory history) {
        log.info("Saving policy status history");
        return repository.save(PolicyStatusHistoryEntity.toEntity(history)).toDto();
    }

    @Override
    public Page<PolicyStatusHistory> findAllByPolicyId(String policyId, Pageable pageable) {
        log.info("Getting policy status history for policyId {}", policyId);
        return repository.findAllByPolicyId(policyId, pageable)
                .map(PolicyStatusHistoryEntity::toDto);
    }

    @Override
    public PolicyStatusHistory findLatestByPolicyId(String policyId) {
        log.info("Getting latest policy status history for policyId {}", policyId);
        return repository.findTopByPolicyIdOrderByCreatedAtDesc(policyId)
                .map(PolicyStatusHistoryEntity::toDto)
                .orElseThrow(() -> new FunctionalError("No status history found for policyId " + policyId));
    }
}
