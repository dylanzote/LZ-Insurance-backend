package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.ProductCoverage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "product_coverage")
public class ProductCoverageEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "min_limit", precision = 12, scale = 2)
    private BigDecimal minLimit;

    @Column(name = "max_limit", precision = 12, scale = 2)
    private BigDecimal maxLimit;

    @Column(name = "default_limit", precision = 12, scale = 2)
    private BigDecimal defaultLimit;

    @Column(precision = 12, scale = 2)
    private BigDecimal deductible;

    @Column(nullable = false)
    private boolean mandatory;

    public static ProductCoverageEntity toEntity(ProductCoverage coverage) {
        ProductCoverageEntity entity = new ProductCoverageEntity();
        BeanUtils.copyProperties(coverage, entity);
        return entity;
    }

    public ProductCoverage toDto() {
        ProductCoverage dto = new ProductCoverage();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
