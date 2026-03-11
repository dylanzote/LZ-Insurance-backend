package com.zote.policy.service.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCoverage {
    private String id;
    private String productId;
    private String code;
    private String name;
    private String description;
    private BigDecimal minLimit;
    private BigDecimal maxLimit;
    private BigDecimal defaultLimit;
    private BigDecimal deductible;
    private boolean mandatory;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
