package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.domain.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepositoryPort {

    Payment savePayment(Payment payment);

    Payment findById(String id);

    void deleteById(String id);

    Page<Payment> findAllByPolicyId(String policyId, Pageable pageable);

    List<Payment> findAllByPolicyIdOrderByPaymentDateDesc(String policyId);

    Payment findByPolicyIdAndTransactionId(String policyId, String transactionId);

    boolean existsByTransactionId(String transactionId);

    Page<Payment> findAllByPaymentDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    List<Payment> findAllByPolicyIdAndStatus(String policyId, PaymentRecordStatus status);

    BigDecimal getTotalPaidAmount(String policyId);
    boolean hasPaidInstallment(String policyId, int installmentNo);
}
