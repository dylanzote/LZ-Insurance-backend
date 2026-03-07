package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.BillingStatus;
import com.zote.policy.service.domain.models.BillingSchedule;
import com.zote.policy.service.domain.ports.outbound.BillingScheduleRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.BillingScheduleEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.BillingScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BillingScheduleRepositoryPortImpl implements BillingScheduleRepositoryPort {

    private final BillingScheduleRepository billingScheduleRepository;

    @Override
    public BillingSchedule saveBillingSchedule(BillingSchedule billingSchedule) {
        log.info("Saving billing schedule {}", billingSchedule);
        return billingScheduleRepository.save(BillingScheduleEntity.toEntity(billingSchedule)).toDto();
    }

    @Override
    public BillingSchedule findById(String id) {
        log.info("Getting billing schedule with id {}", id);
        return billingScheduleRepository.findById(id)
                .map(BillingScheduleEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find billing schedule with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting billing schedule with id {}", id);
        billingScheduleRepository.deleteById(id);
    }

    @Override
    public List<BillingSchedule> saveAllSchedules(List<BillingSchedule> schedules) {
        log.info("Saving {} billing schedules", schedules.size());
        return billingScheduleRepository.saveAll(schedules.stream().map(BillingScheduleEntity::toEntity).toList())
                .stream()
                .map(BillingScheduleEntity::toDto)
                .toList();
    }

    @Override
    public boolean existsByPolicyId(String policyId) {
        log.info("Checking if billing schedules exist for policyId {}", policyId);
        return billingScheduleRepository.existsByPolicyId(policyId);
    }

    @Override
    public List<BillingSchedule> findAllByPolicyIdOrderByInstallmentNoAsc(String policyId) {
        log.info("Getting billing schedules for policyId {} ordered by installment no", policyId);
        return billingScheduleRepository.findAllByPolicyIdOrderByInstallmentNoAsc(policyId)
                .stream()
                .map(BillingScheduleEntity::toDto)
                .toList();
    }

    @Override
    public BillingSchedule findByPolicyIdAndInstallmentNo(String policyId, int installmentNo) {
        log.info("Getting billing schedule for policyId {} and installmentNo {}", policyId, installmentNo);
        return billingScheduleRepository.findByPolicyIdAndInstallmentNo(policyId, installmentNo)
                .map(BillingScheduleEntity::toDto)
                .orElseThrow(() -> new FunctionalError(
                        "could not find billing schedule for policyId " + policyId + " and installmentNo " + installmentNo
                ));
    }

    @Override
    public Page<BillingSchedule> findAllByStatus(BillingStatus status, Pageable pageable) {
        log.info("Getting billing schedules by status {}", status);
        return billingScheduleRepository.findAllByStatus(status, pageable).map(BillingScheduleEntity::toDto);
    }

    @Override
    public Page<BillingSchedule> findAllByDueDateBetween(LocalDate from, LocalDate to, Pageable pageable) {
        log.info("Getting billing schedules by due date between {} and {}", from, to);
        return billingScheduleRepository.findAllByDueDateBetween(from, to, pageable).map(BillingScheduleEntity::toDto);
    }

    @Override
    public Page<BillingSchedule> findAllByPolicyIdAndStatus(String policyId, BillingStatus status, Pageable pageable) {
        log.info("Getting billing schedules for policyId {} by status {}", policyId, status);
        return billingScheduleRepository.findAllByPolicyIdAndStatus(policyId, status, pageable)
                .map(BillingScheduleEntity::toDto);
    }

    @Override
    public long countByPolicyIdAndStatus(String policyId, BillingStatus status) {
        log.info("Counting billing schedules for policyId {} by status {}", policyId, status);
        return billingScheduleRepository.countByPolicyIdAndStatus(policyId, status);
    }
}
