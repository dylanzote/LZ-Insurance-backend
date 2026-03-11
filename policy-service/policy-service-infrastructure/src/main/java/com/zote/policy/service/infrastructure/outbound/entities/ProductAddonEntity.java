package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.models.ProductAddon;
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
@Table(name = "product_addon")
public class ProductAddonEntity extends Auditable {

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

    @Column(name = "additional_premium", precision = 12, scale = 2)
    private BigDecimal additionalPremium;

    @Column(nullable = false)
    private boolean optional;

    public static ProductAddonEntity toEntity(ProductAddon addon) {
        ProductAddonEntity entity = new ProductAddonEntity();
        BeanUtils.copyProperties(addon, entity);
        return entity;
    }

    public ProductAddon toDto() {
        ProductAddon dto = new ProductAddon();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
