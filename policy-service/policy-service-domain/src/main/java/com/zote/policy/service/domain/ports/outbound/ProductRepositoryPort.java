package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryPort {

    Product saveProduct(Product product);

    Product findById(String id);

    Product findByCode(String code);

    boolean existsByCode(String code);

    Page<Product> findAllActive(Pageable pageable);

    Page<Product> findAllByPolicyTypeAndActiveTrue(PolicyType policyType, Pageable pageable);

    ProductCoverage saveCoverage(ProductCoverage coverage);

    List<ProductCoverage> findAllCoveragesByProductId(String productId);

    ProductCoverage findCoverageByProductIdAndCode(String productId, String code);

    ProductRatingFactor saveRatingFactor(ProductRatingFactor factor);

    List<ProductRatingFactor> findAllRatingFactorsByProductId(String productId);

    List<ProductRatingFactor> findRequiredRatingFactorsByProductId(String productId);

    ProductAddon saveAddon(ProductAddon addon);

    List<ProductAddon> findAllAddonsByProductId(String productId);

    boolean existsCoverageByProductIdAndCode(String productId, String code);

    ProductCoverage findCoverageById(String id);

    boolean existsRatingFactorByProductIdAndCode(String productId, String code);

    ProductRatingFactor findRatingFactorById(String id);

    boolean existsAddonByProductIdAndCode(String productId, String code);

    ProductAddon findAddonById(String id);
}
