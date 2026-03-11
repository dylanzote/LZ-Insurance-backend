package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.models.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteRepositoryPort {

    Quote saveQuote(Quote quote);

    Quote findById(String id);

    Quote findByQuoteNumber(String quoteNumber);

    boolean existsByQuoteNumber(String quoteNumber);

    Page<Quote> findAllByCustomerId(String customerId, Pageable pageable);

    Page<Quote> findAllByStatus(QuoteStatus status, Pageable pageable);

    Page<Quote> findAllByCustomerIdAndStatus(String customerId, QuoteStatus status, Pageable pageable);

    /** Find renewal quotes (with parentPolicyId) by policy and status */
    java.util.List<Quote> findByParentPolicyIdAndStatus(String parentPolicyId, QuoteStatus status);
}
