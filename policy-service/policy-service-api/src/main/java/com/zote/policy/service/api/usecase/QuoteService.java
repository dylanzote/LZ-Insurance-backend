package com.zote.policy.service.api.usecase;

import com.zote.policy.service.api.controller.QuoteApi;
import com.zote.policy.service.api.request.AcceptQuoteRequest;
import com.zote.policy.service.api.request.AddUnderwritingNoteRequest;
import com.zote.policy.service.api.request.CreateQuoteRequest;
import com.zote.policy.service.api.request.UpdateQuoteRequest;
import com.zote.policy.service.api.request.UnderwritingDecisionRequest;
import com.zote.policy.service.api.response.QuotePageResponse;
import com.zote.policy.service.api.response.QuoteResponse;
import com.zote.policy.service.api.response.UnderwritingNoteResponse;
import com.zote.policy.service.domain.ports.inbound.QuotePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteService implements QuoteApi {

    private final QuotePort quotePort;

    @Override
    public QuoteResponse createQuote(CreateQuoteRequest request) {
        log.info("Creating quote for product: {}, customer: {}", request.getProductId(), request.getCustomerId());
        return QuoteResponse.fromQuote(quotePort.createQuote(request.toData()));
    }

    @Override
    public QuoteResponse updateQuote(UpdateQuoteRequest request) {
        log.info("Updating quote: {}", request.getQuoteId());
        return QuoteResponse.fromQuote(quotePort.updateQuote(request.toData()));
    }

    @Override
    public QuoteResponse acceptQuote(AcceptQuoteRequest request) {
        log.info("Accepting quote: {}", request.getQuoteId());
        return QuoteResponse.fromQuote(quotePort.acceptQuote(request.toData()));
    }

    @Override
    public QuoteResponse recordUnderwritingDecision(UnderwritingDecisionRequest request) {
        log.info("Recording underwriting decision for quote: {}", request.getQuoteId());
        return QuoteResponse.fromQuote(quotePort.recordUnderwritingDecision(request.toData()));
    }

    @Override
    public UnderwritingNoteResponse addUnderwritingNote(AddUnderwritingNoteRequest request) {
        log.info("Adding underwriting note for quote: {}", request.getQuoteId());
        return UnderwritingNoteResponse.from(quotePort.addUnderwritingNote(request.toData()));
    }

    @Override
    public List<UnderwritingNoteResponse> getUnderwritingNotes(String quoteId) {
        log.info("Getting underwriting notes for quote: {}", quoteId);
        return quotePort.getUnderwritingNotes(quoteId).stream()
                .map(UnderwritingNoteResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public QuoteResponse getQuoteById(String quoteId) {
        log.info("Getting quote by id: {}", quoteId);
        return QuoteResponse.fromQuote(quotePort.findQuoteById(quoteId));
    }

    @Override
    public QuoteResponse getQuoteByNumber(String quoteNumber) {
        log.info("Getting quote by number: {}", quoteNumber);
        return QuoteResponse.fromQuote(quotePort.findQuoteByQuoteNumber(quoteNumber));
    }

    @Override
    public QuotePageResponse getQuotesByCustomer(String customerId, int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Getting quotes for customer: {}", customerId);
        var pageResult = quotePort.getQuotesByCustomer(customerId, page, size, sortField, sortDirection);
        return new QuotePageResponse(pageResult.map(QuoteResponse::fromQuote));
    }
}
