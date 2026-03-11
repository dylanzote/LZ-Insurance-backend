package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import com.zote.policy.service.domain.ports.inbound.ProductPort;
import com.zote.policy.service.domain.ports.outbound.ProductRepositoryPort;
import com.zote.policy.service.domain.support.ProductBuilderSupport;
import com.zote.policy.service.domain.support.ProductSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductImpl implements ProductPort {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductSupport productSupport;

    @Override
    public Product saveProduct(Product product) {
        log.info("Saving product with code {}", product.getCode());
        productSupport.validateProduct(product);

        if (productRepositoryPort.existsByCode(product.getCode())) {
            throw new FunctionalError("product code already exists: " + product.getCode());
        }

        ProductBuilderSupport.ensureNewProductDefaults(product);

        return productRepositoryPort.saveProduct(product);
    }

    @Override
    public Product updateProduct(Product product) {
        log.info("Updating product with id {}", product.getId());
        productSupport.validateProduct(product);

        var existing = productRepositoryPort.findById(product.getId());

        if (!existing.getCode().equals(product.getCode())
                && productRepositoryPort.existsByCode(product.getCode())) {
            throw new FunctionalError("product code already exists: " + product.getCode());
        }

        return productRepositoryPort.saveProduct(product);
    }

    @Override
    public void activateProduct(String productId) {
        log.info("Activating product {}", productId);
        var product = productRepositoryPort.findById(productId);
        product.setActive(true);
        productRepositoryPort.saveProduct(product);
    }

    @Override
    public void deactivateProduct(String productId) {
        log.info("Deactivating product {}", productId);
        productSupport.validateProductId(productId);
        var product = productRepositoryPort.findById(productId);
        product.setActive(false);
        productRepositoryPort.saveProduct(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findProductById(String productId) {
        log.info("Finding product by id {}", productId);
        return productRepositoryPort.findById(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findProductByCode(String code) {
        log.info("Finding product by code {}", code);
        return productRepositoryPort.findByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllActiveProducts(int page, int size, String sortField, Sort.Direction direction) {
        int pageNo = page < 0 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNo, size, direction, sortField);
        return productRepositoryPort.findAllActive(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllActiveProductsByPolicyType(PolicyType policyType, int page, int size, String sortField, Sort.Direction direction) {
        int pageNo = page < 0 ? 0 : page - 1;
        Pageable pageable = PageRequest.of(pageNo, size, direction, sortField);
        return productRepositoryPort.findAllByPolicyTypeAndActiveTrue(policyType, pageable);
    }

    @Override
    public ProductCoverage saveCoverage(ProductCoverage coverage) {
        log.info("Saving coverage {} for productId {}", coverage.getCode(), coverage.getProductId());
        productSupport.validateCoverage(coverage);

        productRepositoryPort.findById(coverage.getProductId());

        if (productRepositoryPort.existsCoverageByProductIdAndCode(coverage.getProductId(), coverage.getCode())) {
            throw new FunctionalError("coverage code already exists for product: " + coverage.getCode());
        }

        return productRepositoryPort.saveCoverage(coverage);
    }

    @Override
    public ProductCoverage updateCoverage(ProductCoverage coverage) {
        log.info("Updating coverage {} for productId {}", coverage.getCode(), coverage.getProductId());
        productSupport.validateCoverage(coverage);

        productRepositoryPort.findCoverageById(coverage.getId());

        return productRepositoryPort.saveCoverage(coverage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductCoverage> findAllCoveragesByProductId(String productId) {
        return productRepositoryPort.findAllCoveragesByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCoverage findCoverageByProductIdAndCode(String productId, String code) {
        return productRepositoryPort.findCoverageByProductIdAndCode(productId, code);
    }

    @Override
    public ProductRatingFactor saveRatingFactor(ProductRatingFactor factor) {
        log.info("Saving rating factor {} for productId {}", factor.getCode(), factor.getProductId());
        productSupport.validateRatingFactor(factor);

        productRepositoryPort.findById(factor.getProductId());

        if (productRepositoryPort.existsRatingFactorByProductIdAndCode(factor.getProductId(), factor.getCode())) {
            throw new FunctionalError("rating factor code already exists for product: " + factor.getCode());
        }

        return productRepositoryPort.saveRatingFactor(factor);
    }

    @Override
    public ProductRatingFactor updateRatingFactor(ProductRatingFactor factor) {
        log.info("Updating rating factor {} for productId {}", factor.getCode(), factor.getProductId());
        productSupport.validateRatingFactor(factor);

        productRepositoryPort.findRatingFactorById(factor.getId());

        return productRepositoryPort.saveRatingFactor(factor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRatingFactor> findAllRatingFactorsByProductId(String productId) {
        return productRepositoryPort.findAllRatingFactorsByProductId(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRatingFactor> findRequiredRatingFactorsByProductId(String productId) {
        return productRepositoryPort.findRequiredRatingFactorsByProductId(productId);
    }

    @Override
    public ProductAddon saveAddon(ProductAddon addon) {
        log.info("Saving addon {} for productId {}", addon.getCode(), addon.getProductId());
        productSupport.validateAddon(addon);

        productRepositoryPort.findById(addon.getProductId());

        if (productRepositoryPort.existsAddonByProductIdAndCode(addon.getProductId(), addon.getCode())) {
            throw new FunctionalError("addon code already exists for product: " + addon.getCode());
        }

        return productRepositoryPort.saveAddon(addon);
    }

    @Override
    public ProductAddon updateAddon(ProductAddon addon) {
        log.info("Updating addon {} for productId {}", addon.getCode(), addon.getProductId());
        productSupport.validateAddon(addon);

        productRepositoryPort.findAddonById(addon.getId());

        return productRepositoryPort.saveAddon(addon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAddon> findAllAddonsByProductId(String productId) {
        return productRepositoryPort.findAllAddonsByProductId(productId);
    }
}
