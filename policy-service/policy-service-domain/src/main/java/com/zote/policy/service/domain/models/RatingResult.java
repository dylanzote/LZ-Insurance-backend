package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResult {
    private BigDecimal premiumBeforeTax;
    private BigDecimal taxAmount;
    private BigDecimal premiumTotal;
    List<CoveragePremium> coveragePremiums;
}
