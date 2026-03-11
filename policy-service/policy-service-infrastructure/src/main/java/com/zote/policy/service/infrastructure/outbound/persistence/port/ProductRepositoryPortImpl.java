package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import com.zote.policy.service.domain.ports.outbound.ProductRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.ProductAddonEntity;
import com.zote.policy.service.infrastructure.outbound.entities.ProductCoverageEntity;
import com.zote.policy.service.infrastructure.outbound.entities.ProductEntity;
import com.zote.policy.service.infrastructure.outbound.entities.ProductRatingFactorEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.ProductAddonRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.ProductCoverageRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.ProductRatingFactorRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductRepositoryPortImpl implements ProductRepositoryPort {

    private final ProductRepository productRepository;
    private final ProductCoverageRepository productCoverageRepository;
    private final ProductRatingFactorRepository productRatingFactorRepository;
    private final ProductAddonRepository productAddonRepository;

    @Override
    public Product saveProduct(Product product) {
        log.info("Saving product {}", product);
        return productRepository.save(ProductEntity.toEntity(product)).toDto();
    }

    @Override
    public Product findById(String id) {
        log.info("Getting product by id {}", id);
        Product product = productRepository.findById(id)
                .map(ProductEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find product with id " + id));

        enrichProduct(product);
        return product;
    }

    @Override
    public Product findByCode(String code) {
        log.info("Getting product by code {}", code);
        Product product = productRepository.findByCode(code)
                .map(ProductEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find product with code " + code));

        enrichProduct(product);
        return product;
    }

    @Override
    public boolean existsByCode(String code) {
        log.info("Checking if product exists by code {}", code);
        return productRepository.existsByCode(code);
    }

    @Override
    public Page<Product> findAllActive(Pageable pageable) {
        log.info("Getting all active products");
        return productRepository.findAllByActiveTrue(pageable).map(entity -> {
            Product product = entity.toDto();
            enrichProduct(product);
            return product;
        });
    }

    @Override
    public Page<Product> findAllByPolicyTypeAndActiveTrue(PolicyType policyType, Pageable pageable) {
        log.info("Getting active products by policyType {}", policyType);
        return productRepository.findAllByPolicyTypeAndActiveTrue(policyType, pageable).map(entity -> {
            Product product = entity.toDto();
            enrichProduct(product);
            return product;
        });
    }

    @Override
    public ProductCoverage saveCoverage(ProductCoverage coverage) {
        log.info("Saving product coverage {}", coverage);
        return productCoverageRepository.save(ProductCoverageEntity.toEntity(coverage)).toDto();
    }

    @Override
    public List<ProductCoverage> findAllCoveragesByProductId(String productId) {
        log.info("Getting product coverages for productId {}", productId);
        return productCoverageRepository.findAllByProductId(productId)
                .stream()
                .map(ProductCoverageEntity::toDto)
                .toList();
    }

    @Override
    public ProductCoverage findCoverageByProductIdAndCode(String productId, String code) {
        log.info("Getting product coverage by productId {} and code {}", productId, code);
        return productCoverageRepository.findByProductIdAndCode(productId, code)
                .map(ProductCoverageEntity::toDto)
                .orElseThrow(() -> new FunctionalError(
                        "could not find coverage with code " + code + " for productId " + productId
                ));
    }

    @Override
    public ProductRatingFactor saveRatingFactor(ProductRatingFactor factor) {
        log.info("Saving product rating factor {}", factor);
        return productRatingFactorRepository.save(ProductRatingFactorEntity.toEntity(factor)).toDto();
    }

    @Override
    public List<ProductRatingFactor> findAllRatingFactorsByProductId(String productId) {
        log.info("Getting rating factors for productId {}", productId);
        return productRatingFactorRepository.findAllByProductId(productId)
                .stream()
                .map(ProductRatingFactorEntity::toDto)
                .toList();
    }

    @Override
    public List<ProductRatingFactor> findRequiredRatingFactorsByProductId(String productId) {
        log.info("Getting required rating factors for productId {}", productId);
        return productRatingFactorRepository.findAllByProductIdAndRequiredTrue(productId)
                .stream()
                .map(ProductRatingFactorEntity::toDto)
                .toList();
    }

    @Override
    public ProductAddon saveAddon(ProductAddon addon) {
        log.info("Saving product addon {}", addon);
        return productAddonRepository.save(ProductAddonEntity.toEntity(addon)).toDto();
    }

    @Override
    public List<ProductAddon> findAllAddonsByProductId(String productId) {
        log.info("Getting addons for productId {}", productId);
        return productAddonRepository.findAllByProductId(productId)
                .stream()
                .map(ProductAddonEntity::toDto)
                .toList();
    }

    @Override
    public boolean existsCoverageByProductIdAndCode(String productId, String code) {
        log.info("Checking if coverage exists for productId {} and code {}", productId, code);
        return productCoverageRepository.existsByProductIdAndCode(productId, code);
    }

    @Override
    public ProductCoverage findCoverageById(String id) {
        log.info("Getting coverage by id {}", id);
        return productCoverageRepository.findById(id)
                .map(ProductCoverageEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find coverage with id " + id));
    }

    @Override
    public boolean existsRatingFactorByProductIdAndCode(String productId, String code) {
        log.info("Checking if rating factor exists for productId {} and code {}", productId, code);
        return productRatingFactorRepository.existsByProductIdAndCode(productId, code);
    }

    @Override
    public ProductRatingFactor findRatingFactorById(String id) {
        log.info("Getting rating factor by id {}", id);
        return productRatingFactorRepository.findById(id)
                .map(ProductRatingFactorEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find rating factor with id " + id));
    }

    @Override
    public boolean existsAddonByProductIdAndCode(String productId, String code) {
        log.info("Checking if addon exists for productId {} and code {}", productId, code);
        return productAddonRepository.existsByProductIdAndCode(productId, code);
    }

    @Override
    public ProductAddon findAddonById(String id) {
        log.info("Getting addon by id {}", id);
        return productAddonRepository.findById(id)
                .map(ProductAddonEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find addon with id " + id));
    }

    private void enrichProduct(Product product) {
        product.setCoverages(findAllCoveragesByProductId(product.getId()));
        product.setRatingFactors(findAllRatingFactorsByProductId(product.getId()));
        product.setAddons(findAllAddonsByProductId(product.getId()));
    }
}
