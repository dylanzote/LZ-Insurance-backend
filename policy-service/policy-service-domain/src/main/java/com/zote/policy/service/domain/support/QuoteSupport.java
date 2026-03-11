package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.models.data.UpdateQuoteData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuoteSupport {

    public void validateUpdateQuote(UpdateQuoteData data) {
        if (data.getQuoteId() == null || data.getQuoteId().isBlank()) {
            throw new FunctionalError("quoteId is required");
        }
        boolean hasUpdate = data.getRatingData() != null || data.getSelectedCoverages() != null
                || data.getEffectiveDate() != null || data.getBillingPlan() != null;
        if (!hasUpdate) {
            throw new FunctionalError("At least one of ratingData, selectedCoverages, effectiveDate, or billingPlan must be provided");
        }
        if (data.getRatingData() != null && data.getRatingData().isEmpty()) {
            throw new FunctionalError("ratingData cannot be empty when provided");
        }
        if (data.getSelectedCoverages() != null && data.getSelectedCoverages().isEmpty()) {
            throw new FunctionalError("selectedCoverages cannot be empty when provided");
        }
    }

    public void validateCreateQuote(CreateQuoteData data) {
        if (data.getProductId() == null || data.getProductId().isBlank()) {
            throw new FunctionalError("productId is required");
        }
        if (data.getCustomerId() == null || data.getCustomerId().isBlank()) {
            throw new FunctionalError("customerId is required");
        }
        if (data.getEffectiveDate() == null) {
            throw new FunctionalError("effectiveDate is required");
        }
        if (data.getRatingData() == null || data.getRatingData().isEmpty()) {
            throw new FunctionalError("ratingData is required");
        }
        if (data.getSelectedCoverages() == null || data.getSelectedCoverages().isEmpty()) {
            throw new FunctionalError("selectedCoverages are required");
        }
    }

    /**
     * Validates effectiveDate <= expiryDate.
     * @throws FunctionalError if dates are invalid
     */
    public void validateEffectiveExpiryDates(LocalDate effectiveDate, LocalDate expiryDate) {
        if (effectiveDate != null && expiryDate != null && effectiveDate.isAfter(expiryDate)) {
            throw new FunctionalError("effectiveDate must be before or equal to expiryDate");
        }
    }

    public void validateRequiredRatingFactors(Product product, Map<String, Object> ratingData) {
        List<ProductRatingFactor> requiredFactors = product.getRatingFactors()
                .stream()
                .filter(ProductRatingFactor::isRequired)
                .toList();

        for (ProductRatingFactor factor : requiredFactors) {
            if (!ratingData.containsKey(factor.getCode()) || ratingData.get(factor.getCode()) == null) {
                throw new FunctionalError("Missing required rating factor: " + factor.getCode());
            }
        }
    }

    public void validateSelectedCoverages(Product product, List<SelectedCoverage> selectedCoverages) {
        Set<String> productCoverageCodes = product.getCoverages()
                .stream()
                .map(ProductCoverage::getCode)
                .collect(Collectors.toSet());

        for (SelectedCoverage selected : selectedCoverages) {
            if (!productCoverageCodes.contains(selected.getCoverageCode())) {
                throw new FunctionalError("Invalid coverage selected: " + selected.getCoverageCode());
            }

            ProductCoverage productCoverage = product.getCoverages().stream()
                    .filter(c -> c.getCode().equals(selected.getCoverageCode()))
                    .findFirst()
                    .orElseThrow();

            validateCoverageLimits(productCoverage, selected);
        }

        List<String> missingMandatory = product.getCoverages().stream()
                .filter(ProductCoverage::isMandatory)
                .map(ProductCoverage::getCode)
                .filter(code -> selectedCoverages.stream().noneMatch(sc -> sc.getCoverageCode().equals(code)))
                .toList();

        if (!missingMandatory.isEmpty()) {
            throw new FunctionalError("Missing mandatory coverages: " + missingMandatory);
        }
    }

    public void verifyQuote(Quote quote) {
        if (quote.getStatus() != QuoteStatus.ACCEPTED) {
            throw new FunctionalError("Only accepted quotes can create policies");
        }

        if (quote.getUnderwritingDecision() != UnderwritingDecisionStatus.APPROVED) {
            throw new FunctionalError("Quote underwriting must be approved before policy creation");
        }
    }

    private void validateCoverageLimits(ProductCoverage coverage, SelectedCoverage selected) {
        if (selected.getLimit() != null) {
            if (coverage.getMinLimit() != null && selected.getLimit().compareTo(coverage.getMinLimit()) < 0) {
                throw new FunctionalError("Coverage limit below minimum for " + coverage.getCode());
            }
            if (coverage.getMaxLimit() != null && selected.getLimit().compareTo(coverage.getMaxLimit()) > 0) {
                throw new FunctionalError("Coverage limit above maximum for " + coverage.getCode());
            }
        }

        if (selected.getDeductible() != null && coverage.getDeductible() != null) {
            if (selected.getDeductible().compareTo(BigDecimal.ZERO) < 0) {
                throw new FunctionalError("Deductible cannot be negative for " + coverage.getCode());
            }
        }
    }

}
