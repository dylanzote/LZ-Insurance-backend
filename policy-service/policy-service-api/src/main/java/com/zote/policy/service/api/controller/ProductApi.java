package com.zote.policy.service.api.controller;

import com.zote.common.utils.models.Permissions;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Product API")
@RestController
@RequestMapping("/product/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface ProductApi {

    @Operation(summary = "Save product")
    @PostMapping("save")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductResponse saveProduct(@Valid @RequestBody Product product);

    @Operation(summary = "Update product")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductResponse updateProduct(@Valid @RequestBody Product product);

    @Operation(summary = "Activate product")
    @PutMapping("activate/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void activateProduct(@PathVariable("productId") String productId);

    @Operation(summary = "Deactivate product")
    @PutMapping("deactivate/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN})
    void deactivateProduct(@PathVariable("productId") String productId);

    @Operation(summary = "Get product by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductResponse getProductById(@PathVariable("id") String productId);

    @Operation(summary = "Get product by code")
    @GetMapping("get-by-code/{code}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductResponse getProductByCode(@PathVariable("code") String code);

    @Operation(summary = "Get all active products")
    @GetMapping("get-all-active")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductPageResponse getAllActiveProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "Get active products by policy type")
    @GetMapping("get-all-active/type/{policyType}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductPageResponse getAllActiveProductsByPolicyType(
            @PathVariable("policyType") PolicyType policyType,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortField", defaultValue = "createdAt") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "DESC") Sort.Direction sortDirection);

    @Operation(summary = "Save coverage")
    @PostMapping("coverage/save")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductCoverageResponse saveCoverage(@Valid @RequestBody ProductCoverage coverage);

    @Operation(summary = "Update coverage")
    @PutMapping("coverage/update")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductCoverageResponse updateCoverage(@Valid @RequestBody ProductCoverage coverage);

    @Operation(summary = "Get coverages by product id")
    @GetMapping("coverage/product/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<ProductCoverageResponse> getCoveragesByProductId(@PathVariable("productId") String productId);

    @Operation(summary = "Get coverage by product id and code")
    @GetMapping("coverage/product/{productId}/code/{code}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductCoverageResponse getCoverageByProductIdAndCode(
            @PathVariable("productId") String productId,
            @PathVariable("code") String code);

    @Operation(summary = "Save rating factor")
    @PostMapping("rating-factor/save")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductRatingFactorResponse saveRatingFactor(@Valid @RequestBody ProductRatingFactor factor);

    @Operation(summary = "Update rating factor")
    @PutMapping("rating-factor/update")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductRatingFactorResponse updateRatingFactor(@Valid @RequestBody ProductRatingFactor factor);

    @Operation(summary = "Get rating factors by product id")
    @GetMapping("rating-factor/product/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<ProductRatingFactorResponse> getRatingFactorsByProductId(@PathVariable("productId") String productId);

    @Operation(summary = "Get required rating factors by product id")
    @GetMapping("rating-factor/product/{productId}/required")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<ProductRatingFactorResponse> getRequiredRatingFactorsByProductId(@PathVariable("productId") String productId);

    @Operation(summary = "Save addon")
    @PostMapping("addon/save")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductAddonResponse saveAddon(@Valid @RequestBody ProductAddon addon);

    @Operation(summary = "Update addon")
    @PutMapping("addon/update")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductAddonResponse updateAddon(@Valid @RequestBody ProductAddon addon);

    @Operation(summary = "Get addons by product id")
    @GetMapping("addon/product/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<ProductAddonResponse> getAddonsByProductId(@PathVariable("productId") String productId);
}
