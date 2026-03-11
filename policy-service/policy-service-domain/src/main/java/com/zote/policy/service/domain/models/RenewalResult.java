package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewalResult {
    private String policyId;
    private String renewalQuoteId;
    private BigDecimal renewalPremium;
    private LocalDate newExpiryDate;
}
