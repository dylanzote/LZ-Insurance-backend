package com.zote.policy.service.api.usecase;

import com.zote.policy.service.api.controller.ProductConfigApi;
import com.zote.policy.service.api.request.AddRequiredDocumentRequest;
import com.zote.policy.service.api.request.CreateProductConfigRequest;
import com.zote.policy.service.api.request.RemoveRequiredDocumentRequest;
import com.zote.policy.service.api.request.UpdateProductConfigRequest;
import com.zote.policy.service.api.response.ProductConfigResponse;
import com.zote.policy.service.api.response.RequiredDocumentResponse;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.ports.inbound.ProductConfigPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductConfigService implements ProductConfigApi {

    private final ProductConfigPort productConfigPort;

    @Override
    public ProductConfigResponse createProductConfig(CreateProductConfigRequest request) {
        log.info("Creating product config for product: {}", request.getProductId());
        return ProductConfigResponse.from(productConfigPort.createProductConfig(request.toData()));
    }

    @Override
    public ProductConfigResponse updateProductConfig(UpdateProductConfigRequest request) {
        log.info("Updating product config: {}", request.getId());
        return ProductConfigResponse.from(productConfigPort.updateProductConfig(request.toData()));
    }

    @Override
    public ProductConfigResponse getProductConfigById(String id) {
        log.info("Getting product config by id: {}", id);
        return ProductConfigResponse.from(productConfigPort.findById(id));
    }

    @Override
    public ProductConfigResponse getProductConfigByProductId(String productId) {
        log.info("Getting product config by product id: {}", productId);
        return ProductConfigResponse.from(productConfigPort.findByProductId(productId));
    }

    @Override
    public List<ProductConfigResponse> getProductConfigsByPolicyType(PolicyType policyType) {
        log.info("Getting product configs by policy type: {}", policyType);
        return productConfigPort.findAllByPolicyType(policyType).stream()
                .map(ProductConfigResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public RequiredDocumentResponse addRequiredDocument(AddRequiredDocumentRequest request) {
        log.info("Adding required document {} to product config: {}", request.getDocumentType(), request.getProductConfigId());
        return RequiredDocumentResponse.from(productConfigPort.addRequiredDocument(request.toData()));
    }

    @Override
    public void removeRequiredDocument(RemoveRequiredDocumentRequest request) {
        log.info("Removing required document {} from product config: {}", request.getDocumentType(), request.getProductConfigId());
        productConfigPort.removeRequiredDocument(request.toData());
    }

    @Override
    public List<RequiredDocumentResponse> getRequiredDocuments(String productConfigId) {
        log.info("Getting required documents for product config: {}", productConfigId);
        return productConfigPort.getRequiredDocuments(productConfigId).stream()
                .map(RequiredDocumentResponse::from)
                .collect(Collectors.toList());
    }
}
