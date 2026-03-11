package com.zote.policy.service.domain.ports.outbound;

import java.math.BigDecimal;

/**
 * Provides configurable rating formula constants.
 * Implemented in infrastructure using application config.
 */
public interface RatingDefaultsPort {

    /** Base premium for coverage calculation. */
    BigDecimal getBasePremium();

    /** Default limit divisor (e.g. 100000 for limit-based rating). */
    BigDecimal getDefaultLimit();

    /** Deductible discount factor when deductible > 0 (e.g. 0.95 = 5% discount). */
    BigDecimal getDeductibleDiscountFactor();

    /** Tax rate for premium (e.g. 0.1925 for 19.25% VAT). */
    BigDecimal getRatingTaxRate();
}
