package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PolicyRepositoryPort {

    Policy savePolicy(Policy policy);

    Policy findById(String id);

    void deleteById(String id);

    Policy findByPolicyNumber(String policyNumber);

    boolean existsByPolicyNumber(String policyNumber);

    Page<Policy> findAllByCustomerId(String customerId, Pageable pageable);

    Page<Policy> findAllByAgentId(String agentId, Pageable pageable);

    Page<Policy> findAllByBranchId(String branchId, Pageable pageable);

    Page<Policy> findAllByStatus(PolicyStatus status, Pageable pageable);

    Page<Policy> findAllByCustomerIdAndStatus(String customerId, PolicyStatus status, Pageable pageable);

    Page<Policy> findActiveOnDate(LocalDate date, Pageable pageable);

    Page<Policy> findExpiringBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<Policy> searchCustomerPolicies(String customerId, String query, Pageable pageable);

    int updateStatus(String policyId, PolicyStatus status);
}
