package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.BeanUtils;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "product")
public class ProductEntity extends Auditable {

     @Id
    private String id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 24)
    private PolicyType policyType;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "product_config_id", nullable = false, length = 64)
    private String productConfigId;

    public static ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        BeanUtils.copyProperties(product, entity);
        return entity;
    }

    public Product toDto() {
        Product dto = new Product();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }
}
