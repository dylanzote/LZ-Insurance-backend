package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSupport {

    public void validateProductId(String productId) {
        if (isBlank(productId)) {
            throw new FunctionalError("productId is required");
        }
    }

    public void validateProduct(Product product) {
        log.info("Validating product {}", product);

        if (product == null) {
            throw new FunctionalError("product cannot be null");
        }
        if (isBlank(product.getCode())) {
            throw new FunctionalError("product code is required");
        }
        if (isBlank(product.getName())) {
            throw new FunctionalError("product name is required");
        }
        if (product.getPolicyType() == null) {
            throw new FunctionalError("policyType is required");
        }
        if (isBlank(product.getProductConfigId())) {
            throw new FunctionalError("productConfigId is required");
        }
    }

    public void validateCoverage(ProductCoverage coverage) {
        log.info("Validating product coverage {}", coverage);

        if (coverage == null) {
            throw new FunctionalError("coverage cannot be null");
        }
        if (isBlank(coverage.getProductId())) {
            throw new FunctionalError("productId is required");
        }
        if (isBlank(coverage.getCode())) {
            throw new FunctionalError("coverage code is required");
        }
        if (isBlank(coverage.getName())) {
            throw new FunctionalError("coverage name is required");
        }
        if (coverage.getDefaultLimit() != null && coverage.getMinLimit() != null
                && coverage.getDefaultLimit().compareTo(coverage.getMinLimit()) < 0) {
            throw new FunctionalError("defaultLimit cannot be lower than minLimit");
        }
        if (coverage.getDefaultLimit() != null && coverage.getMaxLimit() != null
                && coverage.getDefaultLimit().compareTo(coverage.getMaxLimit()) > 0) {
            throw new FunctionalError("defaultLimit cannot be greater than maxLimit");
        }
    }

    public void validateRatingFactor(ProductRatingFactor factor) {
        log.info("Validating rating factor {}", factor);

        if (factor == null) {
            throw new FunctionalError("rating factor cannot be null");
        }
        if (isBlank(factor.getProductId())) {
            throw new FunctionalError("productId is required");
        }
        if (isBlank(factor.getCode())) {
            throw new FunctionalError("rating factor code is required");
        }
        if (isBlank(factor.getLabel())) {
            throw new FunctionalError("rating factor label is required");
        }
        if (factor.getDataType() == null) {
            throw new FunctionalError("dataType is required");
        }
    }

    public void validateAddon(ProductAddon addon) {
        log.info("Validating product addon {}", addon);

        if (addon == null) {
            throw new FunctionalError("addon cannot be null");
        }
        if (isBlank(addon.getProductId())) {
            throw new FunctionalError("productId is required");
        }
        if (isBlank(addon.getCode())) {
            throw new FunctionalError("addon code is required");
        }
        if (isBlank(addon.getName())) {
            throw new FunctionalError("addon name is required");
        }
        if (addon.getAdditionalPremium() == null || addon.getAdditionalPremium().signum() < 0) {
            throw new FunctionalError("additionalPremium must be >= 0");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
