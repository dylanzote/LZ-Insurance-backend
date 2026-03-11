package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.ProductCoverage;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductCoverageResponse {

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
    private LocalDateTime updatedAt;

    public static ProductCoverageResponse from(ProductCoverage coverage) {
        ProductCoverageResponse response = new ProductCoverageResponse();
        BeanUtils.copyProperties(coverage, response);
        return response;
    }
}
