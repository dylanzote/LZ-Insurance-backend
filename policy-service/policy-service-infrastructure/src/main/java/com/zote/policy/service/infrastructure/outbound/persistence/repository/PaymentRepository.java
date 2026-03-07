package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.PaymentRecordStatus;
import com.zote.policy.service.infrastructure.outbound.entities.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    Page<PaymentEntity> findAllByPolicyId(String policyId, Pageable pageable);

    List<PaymentEntity> findAllByPolicyIdOrderByPaymentDateDesc(String policyId);

    Optional<PaymentEntity> findByPolicyIdAndTransactionId(String policyId, String transactionId);

    boolean existsByTransactionId(String transactionId);

    Page<PaymentEntity> findAllByPaymentDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    List<PaymentEntity> findAllByPolicyIdAndStatus(String policyId, PaymentRecordStatus status);
}
