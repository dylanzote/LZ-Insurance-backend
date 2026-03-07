package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.domain.models.Payment;
import com.zote.policy.service.domain.ports.outbound.PaymentRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PaymentEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentRepositoryPortImpl  implements PaymentRepositoryPort {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(Payment payment) {
        log.info("Saving payment {}", payment);
        return paymentRepository.save(PaymentEntity.toEntity(payment)).toDto();
    }

    @Override
    public Payment findById(String id) {
        log.info("Getting payment with id {}", id);
        return paymentRepository.findById(id)
                .map(PaymentEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find payment with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting payment with id {}", id);
        paymentRepository.deleteById(id);
    }

    @Override
    public Page<Payment> findAllByPolicyId(String policyId, Pageable pageable) {
        log.info("Getting payments by policyId {}", policyId);
        return paymentRepository.findAllByPolicyId(policyId, pageable)
                .map(PaymentEntity::toDto);
    }

    @Override
    public List<Payment> findAllByPolicyIdOrderByPaymentDateDesc(String policyId) {
        log.info("Getting payments by policyId {} ordered by paymentDate desc", policyId);
        return paymentRepository.findAllByPolicyIdOrderByPaymentDateDesc(policyId)
                .stream()
                .map(PaymentEntity::toDto)
                .toList();
    }

    @Override
    public Payment findByPolicyIdAndTransactionId(String policyId, String transactionId) {
        log.info("Getting payment by policyId {} and transactionId {}", policyId, transactionId);
        return paymentRepository.findByPolicyIdAndTransactionId(policyId, transactionId)
                .map(PaymentEntity::toDto)
                .orElseThrow(() ->
                        new FunctionalError("could not find payment with transactionId " + transactionId)
                );
    }

    @Override
    public boolean existsByTransactionId(String transactionId) {
        log.info("Checking if payment exists with transactionId {}", transactionId);
        return paymentRepository.existsByTransactionId(transactionId);
    }

    @Override
    public Page<Payment> findAllByPaymentDateBetween(LocalDate from, LocalDate to, Pageable pageable) {
        log.info("Getting payments between {} and {}", from, to);
        return paymentRepository.findAllByPaymentDateBetween(from, to, pageable)
                .map(PaymentEntity::toDto);
    }

    @Override
    public List<Payment> findAllByPolicyIdAndStatus(String policyId, PaymentRecordStatus status) {
        log.info("Getting payments by policyId {} and status {}", policyId, status);
        return paymentRepository.findAllByPolicyIdAndStatus(policyId, status)
                .stream()
                .map(PaymentEntity::toDto)
                .toList();
    }

    @Override
    public BigDecimal getTotalPaidAmount(String policyId) {
        log.info("Calculating total paid amount for policyId {}", policyId);
        return paymentRepository.findAllByPolicyIdOrderByPaymentDateDesc(policyId).stream()
                .filter(p -> p.getStatus() == PaymentRecordStatus.CONFIRMED || p.getStatus() == PaymentRecordStatus.RECORDED)
                .map(PaymentEntity::toDto)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public boolean hasPaidInstallment(String policyId, int installmentNo) {
        log.info("Checking if installment {} is paid for policyId {}", installmentNo, policyId);
        return paymentRepository.findAllByPolicyIdOrderByPaymentDateDesc(policyId).stream()
                .map(PaymentEntity::toDto)
                .anyMatch(p -> p.getInstallmentNo() != null
                        && p.getInstallmentNo() == installmentNo
                        && (p.getStatus() == PaymentRecordStatus.CONFIRMED || p.getStatus() == PaymentRecordStatus.RECORDED));
        }
}
