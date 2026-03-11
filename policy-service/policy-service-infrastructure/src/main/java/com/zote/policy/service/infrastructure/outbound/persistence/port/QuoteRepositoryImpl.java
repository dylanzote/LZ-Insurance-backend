package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.ports.outbound.QuoteRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.QuoteEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class QuoteRepositoryImpl implements QuoteRepositoryPort {

    private final QuoteRepository quoteRepository;

    @Override
    public Quote saveQuote(Quote quote) {
        log.info("Saving quote {}", quote);
        return quoteRepository.save(QuoteEntity.toEntity(quote)).toDto();
    }

    @Override
    public Quote findById(String id) {
        log.info("Getting quote with id {}", id);
        return quoteRepository.findById(id)
                .map(QuoteEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find quote with id " + id));
    }

    @Override
    public Quote findByQuoteNumber(String quoteNumber) {
        log.info("Getting quote with quoteNumber {}", quoteNumber);
        return quoteRepository.findByQuoteNumber(quoteNumber)
                .map(QuoteEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find quote with quoteNumber " + quoteNumber));
    }

    @Override
    public boolean existsByQuoteNumber(String quoteNumber) {
        log.info("Checking if quote exists by quoteNumber {}", quoteNumber);
        return quoteRepository.existsByQuoteNumber(quoteNumber);
    }

    @Override
    public Page<Quote> findAllByCustomerId(String customerId, Pageable pageable) {
        log.info("Getting quotes by customerId {}", customerId);
        return quoteRepository.findAllByCustomerId(customerId, pageable).map(QuoteEntity::toDto);
    }

    @Override
    public Page<Quote> findAllByStatus(QuoteStatus status, Pageable pageable) {
        log.info("Getting quotes by status {}", status);
        return quoteRepository.findAllByStatus(status, pageable).map(QuoteEntity::toDto);
    }

    @Override
    public Page<Quote> findAllByCustomerIdAndStatus(String customerId, QuoteStatus status, Pageable pageable) {
        log.info("Getting quotes by customerId {} and status {}", customerId, status);
        return quoteRepository.findAllByCustomerIdAndStatus(customerId, status, pageable).map(QuoteEntity::toDto);
    }

    @Override
    public java.util.List<Quote> findByParentPolicyIdAndStatus(String parentPolicyId, QuoteStatus status) {
        return quoteRepository.findByParentPolicyIdAndStatus(parentPolicyId, status).stream()
                .map(QuoteEntity::toDto)
                .toList();
    }
}
