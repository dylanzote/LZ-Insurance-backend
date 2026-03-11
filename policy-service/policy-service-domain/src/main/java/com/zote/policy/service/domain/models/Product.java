package com.zote.policy.service.domain.models;

import com.zote.policy.service.domain.enums.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String code;
    private String name;
    private String description;
    private PolicyType policyType;
    private boolean active;
    private String productConfigId;
    private List<ProductCoverage> coverages;
    private List<ProductRatingFactor> ratingFactors;
    private List<ProductAddon> addons;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;
}
