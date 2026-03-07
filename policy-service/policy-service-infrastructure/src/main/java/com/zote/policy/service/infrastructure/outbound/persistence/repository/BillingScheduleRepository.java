package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.infrastructure.outbound.entities.BillingScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillingScheduleRepository extends JpaRepository<BillingScheduleEntity, String> {

    List<BillingScheduleEntity> findAllByPolicyIdOrderByInstallmentNoAsc(String policyId);

    Optional<BillingScheduleEntity> findByPolicyIdAndInstallmentNo(String policyId, int installmentNo);

    Page<BillingScheduleEntity> findAllByStatus(BillingStatus status, Pageable pageable);

    Page<BillingScheduleEntity> findAllByDueDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<BillingScheduleEntity> findAllByPolicyIdAndStatus(String policyId, BillingStatus status, Pageable pageable);

    long countByPolicyIdAndStatus(String policyId, BillingStatus status);

    boolean existsByPolicyId(String policyId);
}
