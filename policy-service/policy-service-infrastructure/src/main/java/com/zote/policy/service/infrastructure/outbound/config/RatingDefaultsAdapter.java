package com.zote.policy.service.infrastructure.outbound.config;

import com.zote.policy.service.domain.ports.outbound.RatingDefaultsPort;
import com.zote.policy.service.infrastructure.config.RatingConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RatingDefaultsAdapter implements RatingDefaultsPort {

    private final RatingConfig config;

    @Override
    public BigDecimal getBasePremium() {
        return config.getBasePremium();
    }

    @Override
    public BigDecimal getDefaultLimit() {
        return config.getDefaultLimit();
    }

    @Override
    public BigDecimal getDeductibleDiscountFactor() {
        return config.getDeductibleDiscountFactor();
    }

    @Override
    public BigDecimal getRatingTaxRate() {
        return config.getTaxRate();
    }
}
