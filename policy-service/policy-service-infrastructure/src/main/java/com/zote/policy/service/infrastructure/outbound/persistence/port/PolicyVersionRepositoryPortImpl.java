package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyVersion;
import com.zote.policy.service.domain.ports.outbound.PolicyVersionRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyVersionEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyVersionRepositoryPortImpl implements PolicyVersionRepositoryPort {

    private final PolicyVersionRepository repository;

    @Override
    public PolicyVersion savePolicyVersion(PolicyVersion version) {
        log.info("Saving policy version for policyId {}", version.getPolicyId());
        return repository.save(PolicyVersionEntity.toEntity(version)).toDto();
    }

    @Override
    public List<PolicyVersion> findAllByPolicyIdOrderByVersionNoDesc(String policyId) {
        log.info("Getting policy versions for policyId {}", policyId);
        return repository.findAllByPolicyIdOrderByVersionNoDesc(policyId)
                .stream()
                .map(PolicyVersionEntity::toDto)
                .toList();
    }

    @Override
    public PolicyVersion findByPolicyIdAndVersionNo(String policyId, int versionNo) {
        log.info("Getting policy version {} for policyId {}", versionNo, policyId);
        return repository.findByPolicyIdAndVersionNo(policyId, versionNo)
                .map(PolicyVersionEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Could not find policy version " + versionNo + " for policyId " + policyId));
    }

    @Override
    public PolicyVersion findCurrentVersion(String policyId) {
        log.info("Getting current policy version for policyId {}", policyId);
        return repository.findCurrentVersion(policyId)
                .map(PolicyVersionEntity::toDto)
                .orElseThrow(() -> new FunctionalError("Could not find current policy version for policyId " + policyId));
    }

    @Override
    public int findMaxVersionNo(String policyId) {
        log.info("Getting max policy versionNo for policyId {}", policyId);
        Integer max = repository.findMaxVersionNo(policyId);
        return max != null ? max : 0;
    }

    @Override
    public boolean existsByPolicyIdAndVersionNo(String policyId, int versionNo) {
        log.info("Checking if version exists for policyId {} and versionNo {}", policyId, versionNo);
        return repository.existsByPolicyIdAndVersionNo(policyId, versionNo);
    }

    @Override
    @Transactional
    public void closeVersion(String versionId, LocalDate effectiveTo) {
        log.info("Closing policy version {} with effectiveTo {}", versionId, effectiveTo);
        int updated = repository.closeVersion(versionId, effectiveTo);
        if (updated == 0) {
            throw new FunctionalError("Could not close policy version with id " + versionId);
        }
    }
}
