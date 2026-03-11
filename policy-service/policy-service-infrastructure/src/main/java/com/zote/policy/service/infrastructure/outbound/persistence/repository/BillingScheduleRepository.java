package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.infrastructure.outbound.entities.BillingScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /** Find distinct policy IDs with overdue billing (for non-payment cancellation 9.7). */
    @Query("SELECT DISTINCT b.policy.id FROM BillingScheduleEntity b WHERE b.status IN ('DUE','OVERDUE') AND b.dueDate <= :threshold")
    List<String> findDistinctPolicyIdsWithOverdueBilling(@Param("threshold") LocalDate threshold);

    /** Find DUE schedules with due date before given date (for marking overdue 10.5). */
    List<BillingScheduleEntity> findByStatusAndDueDateBefore(BillingStatus status, LocalDate date);
