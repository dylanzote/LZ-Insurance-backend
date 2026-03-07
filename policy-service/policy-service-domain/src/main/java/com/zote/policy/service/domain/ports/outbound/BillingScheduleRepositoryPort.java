package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BillingScheduleRepositoryPort {

    BillingSchedule saveBillingSchedule(BillingSchedule billingSchedule);

    BillingSchedule findById(String id);

    void deleteById(String id);

    boolean existsByPolicyId(String policyId);
    List<BillingSchedule> saveAllSchedules(List<BillingSchedule> schedules);

    List<BillingSchedule> findAllByPolicyIdOrderByInstallmentNoAsc(String policyId);

    BillingSchedule findByPolicyIdAndInstallmentNo(String policyId, int installmentNo);

    Page<BillingSchedule> findAllByStatus(BillingStatus status, Pageable pageable);

    Page<BillingSchedule> findAllByDueDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<BillingSchedule> findAllByPolicyIdAndStatus(String policyId, BillingStatus status, Pageable pageable);

    long countByPolicyIdAndStatus(String policyId, BillingStatus status);

}
