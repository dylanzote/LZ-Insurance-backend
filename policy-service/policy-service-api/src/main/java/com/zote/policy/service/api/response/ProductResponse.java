package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.domain.models.ProductAddon;
import com.zote.policy.service.domain.models.ProductCoverage;
import com.zote.policy.service.domain.models.ProductRatingFactor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ProductResponse {

    private String id;
    private String code;
    private String name;
    private String description;
    private PolicyType policyType;
    private boolean active;
    private String productConfigId;
    private List<ProductCoverageResponse> coverages;
    private List<ProductRatingFactorResponse> ratingFactors;
    private List<ProductAddonResponse> addons;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static ProductResponse fromProduct(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response, "coverages", "ratingFactors", "addons");
        if (product.getCoverages() != null) {
            response.setCoverages(product.getCoverages().stream()
                    .map(ProductCoverageResponse::from)
                    .collect(Collectors.toList()));
        }
        if (product.getRatingFactors() != null) {
            response.setRatingFactors(product.getRatingFactors().stream()
                    .map(ProductRatingFactorResponse::from)
                    .collect(Collectors.toList()));
        }
        if (product.getAddons() != null) {
            response.setAddons(product.getAddons().stream()
                    .map(ProductAddonResponse::from)
                    .collect(Collectors.toList()));
        }
        return response;
    }
}
