package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.data.AcceptQuoteData;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.models.data.AddUnderwritingNoteData;
import com.zote.policy.service.domain.models.data.UpdateQuoteApplyData;
import com.zote.policy.service.domain.models.data.UpdateQuoteData;
import com.zote.policy.service.domain.models.data.UnderwritingDecisionData;
import com.zote.policy.service.domain.ports.inbound.QuotePort;
import com.zote.policy.service.domain.ports.outbound.PolicyDefaultsPort;
import com.zote.policy.service.domain.ports.outbound.ProductConfigRepositoryPort;
import com.zote.policy.service.domain.ports.outbound.ProductRepositoryPort;
import com.zote.policy.service.domain.ports.outbound.QuoteRepositoryPort;
import com.zote.policy.service.domain.ports.outbound.UnderwritingNoteRepositoryPort;
import com.zote.policy.service.domain.support.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class QuoteImpl implements QuotePort {

    private final QuoteRepositoryPort quoteRepositoryPort;
    private final UnderwritingNoteRepositoryPort underwritingNoteRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductConfigRepositoryPort productConfigRepositoryPort;
    private final PolicyDefaultsPort policyDefaultsPort;

    private final QuoteSupport quoteSupport;
    private final ProductConfigSupport productConfigSupport;
    private final EligibilitySupport eligibilitySupport;
    private final RatingSupport ratingSupport;
    private final PolicyTermCalculator policyTermCalculator;
    private final MessagingSupport messagingSupport;

    @Override
    public Quote createQuote(CreateQuoteData data) {
        log.info("Creating quote for productId={} customerId={}", data.getProductId(), data.getCustomerId());

        quoteSupport.validateCreateQuote(data);

        Product product = productRepositoryPort.findById(data.getProductId());
        quoteSupport.validateRequiredRatingFactors(product, data.getRatingData());
        quoteSupport.validateSelectedCoverages(product, data.getSelectedCoverages());

        var config = productConfigRepositoryPort.findByProductId(data.getProductId());
        productConfigSupport.validateBillingPlanAgainstConfig(data, config);
        var expiryDate = policyTermCalculator.calculateExpiryDate(data, config);
        quoteSupport.validateEffectiveExpiryDates(data.getEffectiveDate(), expiryDate);

        EligibilityResult eligibility = eligibilitySupport.evaluate(data, config);
        if (!eligibility.isEligible()) {
            throw new FunctionalError("Quote declined by eligibility rules: " + eligibility.getReason());
        }

        RatingResult ratingResult = ratingSupport.calculate(data, product);
        var snapshot = QuoteBuilderSupport.buildSnapshot(data, product);

        int quoteValidityDays = policyDefaultsPort.getQuoteValidityDays();
        String quoteNumberPrefix = policyDefaultsPort.getQuoteNumberPrefix(product.getPolicyType());
        Quote quote = QuoteBuilderSupport.buildQuote(data, product, expiryDate, ratingResult, snapshot, quoteValidityDays, quoteNumberPrefix);

        if (eligibility.isRequiresManualReview()) {
            quote.setStatus(QuoteStatus.UNDER_REVIEW);
            quote.setUnderwritingDecision(UnderwritingDecisionStatus.MANUAL_REVIEW);
        } else {
            quote.setStatus(QuoteStatus.APPROVED);
            quote.setUnderwritingDecision(UnderwritingDecisionStatus.APPROVED);
        }

        quote = quoteRepositoryPort.saveQuote(quote);
        messagingSupport.publishQuoteCreatedEvent(quote);

        log.info("Quote created: {}", quote.getQuoteNumber());
        return quote;
    }

    @Override
    public Quote updateQuote(UpdateQuoteData data) {
        log.info("Updating quote {}", data.getQuoteId());

        quoteSupport.validateUpdateQuote(data);

        Quote quote = quoteRepositoryPort.findById(data.getQuoteId());

        if (quote.getValidUntil() != null && quote.getValidUntil().isBefore(LocalDateTime.now())) {
            quote.setStatus(QuoteStatus.EXPIRED);
            quoteRepositoryPort.saveQuote(quote);
            throw new FunctionalError("Cannot update expired quote");
        }

        if (quote.getStatus() != QuoteStatus.CALCULATED && quote.getStatus() != QuoteStatus.UNDER_REVIEW) {
            throw new FunctionalError("Quote cannot be updated from status: " + quote.getStatus());
        }

        var mergedData = mergeQuoteWithUpdate(quote, data);
        Product product = productRepositoryPort.findById(quote.getProductId());
        quoteSupport.validateRequiredRatingFactors(product, mergedData.getRatingData());
        quoteSupport.validateSelectedCoverages(product, mergedData.getSelectedCoverages());

        var config = productConfigRepositoryPort.findByProductId(quote.getProductId());
        productConfigSupport.validateBillingPlanAgainstConfig(mergedData, config);
        var expiryDate = policyTermCalculator.calculateExpiryDate(mergedData, config);
        quoteSupport.validateEffectiveExpiryDates(mergedData.getEffectiveDate(), expiryDate);

        RatingResult ratingResult = ratingSupport.calculate(mergedData, product);
        var snapshot = QuoteBuilderSupport.buildSnapshot(mergedData, product);

        var applyData = UpdateQuoteApplyData.builder()
                .billingPlan(mergedData.getBillingPlan())
                .effectiveDate(mergedData.getEffectiveDate())
                .expiryDate(expiryDate)
                .ratingData(mergedData.getRatingData())
                .selectedCoverages(mergedData.getSelectedCoverages())
                .coveragePremiums(ratingResult.getCoveragePremiums())
                .premiumBeforeTax(ratingResult.getPremiumBeforeTax())
                .taxAmount(ratingResult.getTaxAmount())
                .premiumTotal(ratingResult.getPremiumTotal())
                .snapshot(snapshot)
                .lastModifiedBy(data.getLastModifiedBy())
                .build();

        quote = QuoteBuilderSupport.applyQuoteUpdate(quote, applyData);
        quote = quoteRepositoryPort.saveQuote(quote);

        log.info("Quote updated: {}", quote.getQuoteNumber());
        return quote;
    }

    private CreateQuoteData mergeQuoteWithUpdate(Quote quote, UpdateQuoteData data) {
        return CreateQuoteData.builder()
                .productId(quote.getProductId())
                .customerId(quote.getCustomerId())
                .agentId(quote.getAgentId())
                .branchId(quote.getBranchId())
                .billingPlan(data.getBillingPlan() != null ? data.getBillingPlan() : quote.getBillingPlan())
                .effectiveDate(data.getEffectiveDate() != null ? data.getEffectiveDate() : quote.getEffectiveDate())
                .ratingData(data.getRatingData() != null ? data.getRatingData() : quote.getRatingData())
                .selectedCoverages(data.getSelectedCoverages() != null ? data.getSelectedCoverages() : quote.getSelectedCoverages())
                .build();
    }

    @Override
    public Quote acceptQuote(AcceptQuoteData data) {
        log.info("Accepting quote {}", data.getQuoteId());

        Quote quote = quoteRepositoryPort.findById(data.getQuoteId());

        if (quote.getValidUntil() != null && quote.getValidUntil().isBefore(LocalDateTime.now())) {
            quote.setStatus(QuoteStatus.EXPIRED);
            quoteRepositoryPort.saveQuote(quote);
            throw new FunctionalError("Cannot accept expired quote");
        }

        if (quote.getStatus() != QuoteStatus.APPROVED) {
            throw new FunctionalError("Quote cannot be accepted from status: " + quote.getStatus());
        }

        quote.setStatus(QuoteStatus.ACCEPTED);
        quote = quoteRepositoryPort.saveQuote(quote);

        messagingSupport.publishQuoteAcceptedEvent(quote);
        return quote;
    }

    @Override
    public Quote recordUnderwritingDecision(UnderwritingDecisionData data) {
        log.info("Recording underwriting decision for quote {}", data.getQuoteId());

        Quote quote = quoteRepositoryPort.findById(data.getQuoteId());

        if (quote.getStatus() != QuoteStatus.UNDER_REVIEW) {
            throw new FunctionalError("Quote is not under review");
        }

        quote.setUnderwritingDecision(data.getDecision());
        quote.setUnderwritingReason(data.getReason());
        quote.setUnderwritingDecidedBy(data.getDecidedBy());
        quote.setUnderwritingDecidedAt(LocalDateTime.now());

        switch (data.getDecision()) {
            case APPROVED -> {
                quote.setStatus(QuoteStatus.APPROVED);
                messagingSupport.publishUnderwritingDecisionEvent(quote);
            }
            case DECLINED -> {
                quote.setStatus(QuoteStatus.DECLINED);
                messagingSupport.publishQuoteRejectedEvent(quote,
                        data.getReason() != null ? data.getReason() : "Underwriting declined");
                messagingSupport.publishUnderwritingDecisionEvent(quote);
            }
            case MANUAL_REVIEW, PENDING -> quote.setStatus(QuoteStatus.UNDER_REVIEW);
        }

        return quoteRepositoryPort.saveQuote(quote);
    }

    @Override
    public UnderwritingNote addUnderwritingNote(AddUnderwritingNoteData data) {
        log.info("Adding underwriting note to quote {}", data.getQuoteId());

        if (data.getQuoteId() == null || data.getQuoteId().isBlank()) {
            throw new FunctionalError("quoteId is required");
        }
        if (data.getNote() == null || data.getNote().isBlank()) {
            throw new FunctionalError("note is required");
        }

        Quote quote = quoteRepositoryPort.findById(data.getQuoteId());
        if (quote.getStatus() != QuoteStatus.UNDER_REVIEW) {
            throw new FunctionalError("Notes can only be added to quotes under review");
        }

        var note = UnderwritingNoteBuilderSupport.buildFromAddData(data);
        return underwritingNoteRepositoryPort.save(note);
    }

    @Override
    public List<UnderwritingNote> getUnderwritingNotes(String quoteId) {
        return underwritingNoteRepositoryPort.findByQuoteId(quoteId);
    }

    @Override
    public Quote findQuoteById(String quoteId) {
        return quoteRepositoryPort.findById(quoteId);
    }

    @Override
    public Quote findQuoteByQuoteNumber(String quoteNumber) {
        return quoteRepositoryPort.findByQuoteNumber(quoteNumber);
    }

    @Override
    public Page<Quote> getQuotesByCustomer(String customerId, int page, int size, String sortField, Sort.Direction direction) {
        int pageNo = page < 0 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNo, size, direction, sortField);
        return quoteRepositoryPort.findAllByCustomerId(customerId, pageable);
    }
}
