package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyCoverage;
import com.zote.policy.service.domain.ports.outbound.PolicyCoverageRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyCoverageEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyCoverageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyCoverageRepositoryPortImpl implements PolicyCoverageRepositoryPort {

    private final PolicyCoverageRepository policyCoverageRepository;

    @Override
    public PolicyCoverage savePolicyCoverage(PolicyCoverage coverage) {
        log.info("Saving policy coverage {}", coverage);
        return policyCoverageRepository.save(PolicyCoverageEntity.toEntity(coverage)).toDto();
    }

    @Override
    public PolicyCoverage findById(String id) {
        log.info("Getting policy coverage with id {}", id);
        return policyCoverageRepository.findById(id)
                .map(PolicyCoverageEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find policy coverage with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting policy coverage with id {}", id);
        policyCoverageRepository.deleteById(id);
    }

    @Override
    public List<PolicyCoverage> findAllByPolicyVersionId(String policyVersionId) {
        log.info("Getting all policy coverages by policyVersionId {}", policyVersionId);
        return policyCoverageRepository.findAllByPolicyVersionId(policyVersionId)
                .stream()
                .map(PolicyCoverageEntity::toDto)
                .toList();
    }

    @Override
    public PolicyCoverage findByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode) {
        log.info("Getting policy coverage by policyVersionId {} and coverageCode {}", policyVersionId, coverageCode);
        return policyCoverageRepository.findByPolicyVersionIdAndCoverageCode(policyVersionId, coverageCode)
                .map(PolicyCoverageEntity::toDto)
                .orElseThrow(() -> new FunctionalError(
                        "could not find policy coverage with coverageCode " + coverageCode +
                                " for policyVersionId " + policyVersionId
                ));
    }

    @Override
    public boolean existsByPolicyVersionIdAndCoverageCode(String policyVersionId, String coverageCode) {
        log.info("Checking if policy coverage exists by policyVersionId {} and coverageCode {}", policyVersionId, coverageCode);
        return policyCoverageRepository.existsByPolicyVersionIdAndCoverageCode(policyVersionId, coverageCode);
    }

    @Override
    public void deleteAllByPolicyVersionId(String policyVersionId) {
        log.info("Deleting all policy coverages for policyVersionId {}", policyVersionId);
        policyCoverageRepository.deleteAllByPolicyVersionId(policyVersionId);
    }
}
