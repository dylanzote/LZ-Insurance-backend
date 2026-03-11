package com.zote.policy.service.domain.ports.inbound;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductPort {
    Product saveProduct(Product product);

    Product updateProduct(Product product);

    void activateProduct(String productId);

    void deactivateProduct(String productId);

    Product findProductById(String productId);

    Product findProductByCode(String code);

    Page<Product> getAllActiveProducts(int page, int size, String sortField, Sort.Direction direction);

    Page<Product> getAllActiveProductsByPolicyType(PolicyType policyType, int page, int size, String sortField, Sort.Direction direction);

    ProductCoverage saveCoverage(ProductCoverage coverage);

    ProductCoverage updateCoverage(ProductCoverage coverage);

    List<ProductCoverage> findAllCoveragesByProductId(String productId);

    ProductCoverage findCoverageByProductIdAndCode(String productId, String code);

    ProductRatingFactor saveRatingFactor(ProductRatingFactor factor);

    ProductRatingFactor updateRatingFactor(ProductRatingFactor factor);

    List<ProductRatingFactor> findAllRatingFactorsByProductId(String productId);

    List<ProductRatingFactor> findRequiredRatingFactorsByProductId(String productId);

    ProductAddon saveAddon(ProductAddon addon);

    ProductAddon updateAddon(ProductAddon addon);

    List<ProductAddon> findAllAddonsByProductId(String productId);

}
