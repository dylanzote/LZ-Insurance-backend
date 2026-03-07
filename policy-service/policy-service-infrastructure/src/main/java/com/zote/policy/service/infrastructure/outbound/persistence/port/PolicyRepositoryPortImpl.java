package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.ports.outbound.PolicyRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyRepositoryPortImpl implements PolicyRepositoryPort {


    private final PolicyRepository policyRepository;

    @Override
    public Policy savePolicy(Policy policy) {
        log.info("Saving policy {}", policy);
        return policyRepository.save(PolicyEntity.toEntity(policy)).toDto();
    }

    @Override
    public Policy findById(String id) {
        log.info("Getting policy with id {}", id);
        return policyRepository.findById(id)
                .map(PolicyEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find policy with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting policy with id {}", id);
        policyRepository.deleteById(id);
    }

    @Override
    public Policy findByPolicyNumber(String policyNumber) {
        log.info("Getting policy with policyNumber {}", policyNumber);
        return policyRepository.findByPolicyNumber(policyNumber)
                .map(PolicyEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find policy with policyNumber " + policyNumber));
    }

    @Override
    public boolean existsByPolicyNumber(String policyNumber) {
        log.info("Checking if policy exists by policyNumber {}", policyNumber);
        return policyRepository.existsByPolicyNumber(policyNumber);
    }

    @Override
    public Page<Policy> findAllByCustomerId(String customerId, Pageable pageable) {
        log.info("Getting policies by customerId {}", customerId);
        return policyRepository.findAllByCustomerId(customerId, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findAllByAgentId(String agentId, Pageable pageable) {
        log.info("Getting policies by agentId {}", agentId);
        return policyRepository.findAllByAgentId(agentId, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findAllByBranchId(String branchId, Pageable pageable) {
        log.info("Getting policies by branchId {}", branchId);
        return policyRepository.findAllByBranchId(branchId, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findAllByStatus(PolicyStatus status, Pageable pageable) {
        log.info("Getting policies by status {}", status);
        return policyRepository.findAllByStatus(status, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findAllByCustomerIdAndStatus(String customerId, PolicyStatus status, Pageable pageable) {
        log.info("Getting policies by customerId {} and status {}", customerId, status);
        return policyRepository.findAllByCustomerIdAndStatus(customerId, status, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findActiveOnDate(LocalDate date, Pageable pageable) {
        log.info("Getting active policies on date {}", date);
        return policyRepository.findActiveOnDate(date, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> findExpiringBetween(LocalDate from, LocalDate to, Pageable pageable) {
        log.info("Getting policies expiring between {} and {}", from, to);
        return policyRepository.findExpiringBetween(from, to, pageable).map(PolicyEntity::toDto);
    }

    @Override
    public Page<Policy> searchCustomerPolicies(String customerId, String query, Pageable pageable) {
        log.info("Searching customer policies for customerId {} with query {}", customerId, query);
        return policyRepository.searchCustomerPolicies(customerId, query, pageable).map(PolicyEntity::toDto);
    }

    @Override
    @Transactional
    public int updateStatus(String policyId, PolicyStatus status) {
        log.info("Updating policy status policyId {} -> {}", policyId, status);
        return policyRepository.updateStatus(policyId, status);
    }
}
