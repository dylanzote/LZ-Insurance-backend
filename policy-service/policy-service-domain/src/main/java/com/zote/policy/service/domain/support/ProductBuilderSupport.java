package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.DataType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Centralized builders for Product domain objects.
 * Keeps construction logic consistent and methods short.
 */
@UtilityClass
public class ProductBuilderSupport {

    /**
     * Applies defaults for a new product before save (e.g. ensure active=true).
     */
    public void ensureNewProductDefaults(Product product) {
        if (product != null && !product.isActive()) {
            product.setActive(true);
        }
    }

    /**
     * Builds a Product with required fields. Use for programmatic creation.
     */
    public Product.ProductBuilder productBuilder(String code, String name, String productConfigId) {
        return Product.builder()
                .id(UUID.randomUUID().toString())
                .code(code)
                .name(name)
                .productConfigId(productConfigId)
                .active(true);
    }

    /**
     * Builds a ProductCoverage with required fields.
     */
    public ProductCoverage.ProductCoverageBuilder coverageBuilder(String productId, String code, String name) {
        return ProductCoverage.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .code(code)
                .name(name);
    }

    /**
     * Builds a ProductRatingFactor with required fields.
     */
    public ProductRatingFactor.ProductRatingFactorBuilder ratingFactorBuilder(String productId, String code, String label, DataType dataType) {
        return ProductRatingFactor.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .code(code)
                .label(label)
                .dataType(dataType);
    }

    /**
     * Builds a ProductAddon with required fields.
     * additionalPremium must be set via builder chain (e.g. .additionalPremium(BigDecimal.ZERO)).
     */
    public ProductAddon.ProductAddonBuilder addonBuilder(String productId, String code, String name, BigDecimal additionalPremium) {
        return ProductAddon.builder()
                .id(UUID.randomUUID().toString())
                .productId(productId)
                .code(code)
                .name(name)
                .additionalPremium(additionalPremium != null ? additionalPremium : BigDecimal.ZERO);
    }
}
