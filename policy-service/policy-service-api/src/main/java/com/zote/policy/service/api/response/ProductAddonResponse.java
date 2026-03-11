package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.ProductAddon;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductAddonResponse {

    private String id;
    private String productId;
    private String code;
    private String name;
    private String description;
    private BigDecimal additionalPremium;
    private boolean optional;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductAddonResponse from(ProductAddon addon) {
        ProductAddonResponse response = new ProductAddonResponse();
        BeanUtils.copyProperties(addon, response);
        return response;
    }
}
