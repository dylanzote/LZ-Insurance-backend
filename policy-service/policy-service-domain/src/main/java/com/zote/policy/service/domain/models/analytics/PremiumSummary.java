package com.zote.policy.service.domain.models.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 16.5 - Premium totals by product, region (branch), or agent for revenue distribution.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumSummary {
    /** Dimension: PRODUCT, BRANCH, AGENT */
    private String dimension;
    /** Rows with dimension value and premium total */
    private List<PremiumSummaryRow> rows;
    private BigDecimal grandTotal;
    private java.time.LocalDateTime asOf;
}
