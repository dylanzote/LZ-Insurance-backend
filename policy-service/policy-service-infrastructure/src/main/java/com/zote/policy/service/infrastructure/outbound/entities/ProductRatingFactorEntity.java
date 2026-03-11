package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.DataType;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "product_rating_factor")
public class ProductRatingFactorEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 24)
    private DataType dataType;

    @Column(nullable = false)
    private boolean required;

    public static ProductRatingFactorEntity toEntity(ProductRatingFactor factor) {
        ProductRatingFactorEntity entity = new ProductRatingFactorEntity();
        BeanUtils.copyProperties(factor, entity);
        return entity;
    }

    public ProductRatingFactor toDto() {
        ProductRatingFactor dto = new ProductRatingFactor();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
