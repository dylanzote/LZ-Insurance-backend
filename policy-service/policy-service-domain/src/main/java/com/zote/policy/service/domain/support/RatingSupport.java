package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.*;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.ports.outbound.RatingDefaultsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingSupport {

    private final RatingDefaultsPort ratingDefaults;

    public RatingResult calculate(CreateQuoteData data, Product product) {
        List<CoveragePremium> coveragePremiums = new ArrayList<>();
        BigDecimal basePremium = BigDecimal.ZERO;

        for (SelectedCoverage selected : data.getSelectedCoverages()) {
            ProductCoverage coverage = product.getCoverages().stream()
                    .filter(c -> c.getCode().equals(selected.getCoverageCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Coverage not found: " + selected.getCoverageCode()));

            BigDecimal premium = calculateCoveragePremium(coverage, selected, data.getRatingData());
            coveragePremiums.add(CoveragePremium.builder()
                    .coverageCode(selected.getCoverageCode())
                    .premium(premium)
                    .build());

            basePremium = basePremium.add(premium);
        }

        BigDecimal tax = calculateTax(basePremium);
        BigDecimal total = basePremium.add(tax);

        return RatingResult.builder()
                .premiumBeforeTax(basePremium)
                .taxAmount(tax)
                .premiumTotal(total)
                .coveragePremiums(coveragePremiums)
                .build();
    }

    public BigDecimal recalculatePremiumFromSnapshot(Map<String, Object> snapshot, BigDecimal currentPremium) {
        if (snapshot == null || snapshot.isEmpty()) {
            return currentPremium;
        }

        BigDecimal recalculatedBase = BigDecimal.ZERO;

        BigDecimal defaultLimit = ratingDefaults.getDefaultLimit();
        BigDecimal basePremium = ratingDefaults.getBasePremium();
        BigDecimal deductibleFactor = ratingDefaults.getDeductibleDiscountFactor();

        Object selectedCoveragesObj = snapshot.get("selectedCoverages");
        if (selectedCoveragesObj instanceof List<?> selectedCoveragesList) {
            for (Object item : selectedCoveragesList) {
                if (item instanceof Map<?, ?> map) {
                    BigDecimal limit = getBigDecimal(map.get("limit"), defaultLimit);
                    BigDecimal deductible = getBigDecimal(map.get("deductible"), BigDecimal.ZERO);

                    BigDecimal limitFactor = limit.divide(defaultLimit, 4, RoundingMode.HALF_UP);

                    BigDecimal deductibleDiscount = BigDecimal.ONE;
                    if (deductible.compareTo(BigDecimal.ZERO) > 0) {
                        deductibleDiscount = deductibleFactor;
                    }

                    BigDecimal coveragePremium = basePremium
                            .multiply(limitFactor)
                            .multiply(deductibleDiscount)
                            .setScale(2, RoundingMode.HALF_UP);

                    recalculatedBase = recalculatedBase.add(coveragePremium);
                }
            }
        }

        if (recalculatedBase.compareTo(BigDecimal.ZERO) == 0) {
            return currentPremium;
        }

        BigDecimal tax = calculateTax(recalculatedBase);
        return recalculatedBase.add(tax);
    }

    private BigDecimal calculateCoveragePremium(ProductCoverage coverage,
                                                SelectedCoverage selected,
                                                Map<String, Object> ratingData) {
        BigDecimal base = ratingDefaults.getBasePremium();
        BigDecimal defaultLimit = ratingDefaults.getDefaultLimit();
        BigDecimal deductibleDiscount = ratingDefaults.getDeductibleDiscountFactor();

        BigDecimal selectedLimit = selected.getLimit() != null
                ? selected.getLimit()
                : coverage.getDefaultLimit() != null ? coverage.getDefaultLimit() : defaultLimit;

        BigDecimal factor = selectedLimit.divide(defaultLimit, 4, RoundingMode.HALF_UP);

        BigDecimal deductibleFactor = BigDecimal.ONE;
        if (selected.getDeductible() != null && selected.getDeductible().compareTo(BigDecimal.ZERO) > 0) {
            deductibleFactor = deductibleDiscount;
        }

        return base.multiply(factor).multiply(deductibleFactor).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTax(BigDecimal amount) {
        return amount.multiply(ratingDefaults.getRatingTaxRate())
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getBigDecimal(Object value, BigDecimal defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
