package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.infrastructure.outbound.entities.QuoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<QuoteEntity, String> {

    Optional<QuoteEntity> findByQuoteNumber(String quoteNumber);

    boolean existsByQuoteNumber(String quoteNumber);

    Page<QuoteEntity> findAllByCustomerId(String customerId, Pageable pageable);

    Page<QuoteEntity> findAllByStatus(QuoteStatus status, Pageable pageable);

    Page<QuoteEntity> findAllByCustomerIdAndStatus(String customerId, QuoteStatus status, Pageable pageable);

    java.util.List<QuoteEntity> findByParentPolicyIdAndStatus(String parentPolicyId, QuoteStatus status);
}
