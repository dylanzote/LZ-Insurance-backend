package com.zote.policy.service.api.usecase;

import com.zote.policy.service.api.controller.ProductApi;
import com.zote.policy.service.api.response.ProductAddonResponse;
import com.zote.policy.service.api.response.ProductCoverageResponse;
import com.zote.policy.service.api.response.ProductPageResponse;
import com.zote.policy.service.api.response.ProductRatingFactorResponse;
import com.zote.policy.service.api.response.ProductResponse;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import com.zote.policy.service.domain.ports.inbound.ProductPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductApi {

    private final ProductPort productPort;

    @Override
    public ProductResponse saveProduct(Product product) {
        log.info("Saving product: {}", product.getCode());
        return ProductResponse.fromProduct(productPort.saveProduct(product));
    }

    @Override
    public ProductResponse updateProduct(Product product) {
        log.info("Updating product: {}", product.getId());
        return ProductResponse.fromProduct(productPort.updateProduct(product));
    }

    @Override
    public void activateProduct(String productId) {
        log.info("Activating product: {}", productId);
        productPort.activateProduct(productId);
    }

    @Override
    public void deactivateProduct(String productId) {
        log.info("Deactivating product: {}", productId);
        productPort.deactivateProduct(productId);
    }

    @Override
    public ProductResponse getProductById(String productId) {
        log.info("Getting product by id: {}", productId);
        return ProductResponse.fromProduct(productPort.findProductById(productId));
    }

    @Override
    public ProductResponse getProductByCode(String code) {
        log.info("Getting product by code: {}", code);
        return ProductResponse.fromProduct(productPort.findProductByCode(code));
    }

    @Override
    public ProductPageResponse getAllActiveProducts(int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Getting all active products");
        var pageResult = productPort.getAllActiveProducts(page, size, sortField, sortDirection);
        return new ProductPageResponse(pageResult.map(ProductResponse::fromProduct));
    }

    @Override
    public ProductPageResponse getAllActiveProductsByPolicyType(PolicyType policyType, int page, int size, String sortField, Sort.Direction sortDirection) {
        log.info("Getting active products by policy type: {}", policyType);
        var pageResult = productPort.getAllActiveProductsByPolicyType(policyType, page, size, sortField, sortDirection);
        return new ProductPageResponse(pageResult.map(ProductResponse::fromProduct));
    }

    @Override
    public ProductCoverageResponse saveCoverage(ProductCoverage coverage) {
        log.info("Saving coverage for product: {}", coverage.getProductId());
        return ProductCoverageResponse.from(productPort.saveCoverage(coverage));
    }

    @Override
    public ProductCoverageResponse updateCoverage(ProductCoverage coverage) {
        log.info("Updating coverage: {}", coverage.getId());
        return ProductCoverageResponse.from(productPort.updateCoverage(coverage));
    }

    @Override
    public List<ProductCoverageResponse> getCoveragesByProductId(String productId) {
        log.info("Getting coverages for product: {}", productId);
        return productPort.findAllCoveragesByProductId(productId).stream()
                .map(ProductCoverageResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ProductCoverageResponse getCoverageByProductIdAndCode(String productId, String code) {
        log.info("Getting coverage for product {} and code {}", productId, code);
        return ProductCoverageResponse.from(productPort.findCoverageByProductIdAndCode(productId, code));
    }

    @Override
    public ProductRatingFactorResponse saveRatingFactor(ProductRatingFactor factor) {
        log.info("Saving rating factor for product: {}", factor.getProductId());
        return ProductRatingFactorResponse.from(productPort.saveRatingFactor(factor));
    }

    @Override
    public ProductRatingFactorResponse updateRatingFactor(ProductRatingFactor factor) {
        log.info("Updating rating factor: {}", factor.getId());
        return ProductRatingFactorResponse.from(productPort.updateRatingFactor(factor));
    }

    @Override
    public List<ProductRatingFactorResponse> getRatingFactorsByProductId(String productId) {
        log.info("Getting rating factors for product: {}", productId);
        return productPort.findAllRatingFactorsByProductId(productId).stream()
                .map(ProductRatingFactorResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductRatingFactorResponse> getRequiredRatingFactorsByProductId(String productId) {
        log.info("Getting required rating factors for product: {}", productId);
        return productPort.findRequiredRatingFactorsByProductId(productId).stream()
                .map(ProductRatingFactorResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public ProductAddonResponse saveAddon(ProductAddon addon) {
        log.info("Saving addon for product: {}", addon.getProductId());
        return ProductAddonResponse.from(productPort.saveAddon(addon));
    }

    @Override
    public ProductAddonResponse updateAddon(ProductAddon addon) {
        log.info("Updating addon: {}", addon.getId());
        return ProductAddonResponse.from(productPort.updateAddon(addon));
    }

    @Override
    public List<ProductAddonResponse> getAddonsByProductId(String productId) {
        log.info("Getting addons for product: {}", productId);
        return productPort.findAllAddonsByProductId(productId).stream()
                .map(ProductAddonResponse::from)
                .collect(Collectors.toList());
    }
}
