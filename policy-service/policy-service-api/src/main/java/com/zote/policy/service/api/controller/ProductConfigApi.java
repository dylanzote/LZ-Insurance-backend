package com.zote.policy.service.api.controller;

import com.zote.common.utils.models.Permissions;
import com.zote.policy.service.api.request.AddRequiredDocumentRequest;
import com.zote.policy.service.api.request.CreateProductConfigRequest;
import com.zote.policy.service.api.request.RemoveRequiredDocumentRequest;
import com.zote.policy.service.api.request.UpdateProductConfigRequest;
import com.zote.policy.service.api.response.ProductConfigResponse;
import com.zote.policy.service.api.response.RequiredDocumentResponse;
import com.zote.policy.service.domain.enums.PolicyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Configuration API")
@RestController
@RequestMapping("/product-config/")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface ProductConfigApi {

    @Operation(summary = "Create product configuration")
    @PostMapping("create")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductConfigResponse createProductConfig(@Valid @RequestBody CreateProductConfigRequest request);

    @Operation(summary = "Update product configuration")
    @PutMapping("update")
    @RolesAllowed({Permissions.IS_ADMIN})
    ProductConfigResponse updateProductConfig(@Valid @RequestBody UpdateProductConfigRequest request);

    @Operation(summary = "Get product config by id")
    @GetMapping("get/{id}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductConfigResponse getProductConfigById(@PathVariable("id") String id);

    @Operation(summary = "Get product config by product id")
    @GetMapping("get-by-product/{productId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    ProductConfigResponse getProductConfigByProductId(@PathVariable("productId") String productId);

    @Operation(summary = "Get product configs by policy type")
    @GetMapping("get-by-type/{policyType}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<ProductConfigResponse> getProductConfigsByPolicyType(@PathVariable("policyType") PolicyType policyType);

    @Operation(summary = "Add required document")
    @PostMapping("required-document/add")
    @RolesAllowed({Permissions.IS_ADMIN})
    RequiredDocumentResponse addRequiredDocument(@Valid @RequestBody AddRequiredDocumentRequest request);

    @Operation(summary = "Remove required document")
    @DeleteMapping("required-document/remove")
    @RolesAllowed({Permissions.IS_ADMIN})
    void removeRequiredDocument(@Valid @RequestBody RemoveRequiredDocumentRequest request);

    @Operation(summary = "Get required documents by product config id")
    @GetMapping("required-document/product-config/{productConfigId}")
    @RolesAllowed({Permissions.IS_ADMIN, Permissions.IS_AGENT, Permissions.IS_CUSTOMER})
    List<RequiredDocumentResponse> getRequiredDocuments(@PathVariable("productConfigId") String productConfigId);
}
