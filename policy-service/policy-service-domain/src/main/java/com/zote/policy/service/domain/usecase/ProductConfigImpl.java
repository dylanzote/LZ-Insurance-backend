package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import com.zote.policy.service.domain.models.data.AddRequiredDocumentData;
import com.zote.policy.service.domain.models.data.CreateProductConfigData;
import com.zote.policy.service.domain.models.data.RemoveRequiredDocumentData;
import com.zote.policy.service.domain.models.data.UpdateProductConfigData;
import com.zote.policy.service.domain.ports.inbound.ProductConfigPort;
import com.zote.policy.service.domain.ports.outbound.ProductConfigRepositoryPort;
import com.zote.policy.service.domain.ports.outbound.ProductRepositoryPort;
import com.zote.policy.service.domain.support.ProductConfigBuilderSupport;
import com.zote.policy.service.domain.support.ProductConfigSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductConfigImpl implements ProductConfigPort {

    private final ProductConfigRepositoryPort productConfigRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductConfigSupport productConfigSupport;

    @Override
    public PolicyProductConfig createProductConfig(CreateProductConfigData data) {
        log.info("Creating product config for productId={}", data.getProductId());

        productConfigSupport.validateCreateProductConfig(data);
        productRepositoryPort.findById(data.getProductId());

        if (productConfigRepositoryPort.existsByProductId(data.getProductId())) {
            throw new FunctionalError("Product config already exists for product " + data.getProductId());
        }

        var config = ProductConfigBuilderSupport.buildProductConfigFromCreate(data);
        return productConfigRepositoryPort.saveProductConfig(config);
    }

    @Override
    public PolicyProductConfig updateProductConfig(UpdateProductConfigData data) {
        log.info("Updating product config id={}", data.getId());

        productConfigSupport.validateUpdateProductConfig(data);
        var existing = productConfigRepositoryPort.findById(data.getId());
        productRepositoryPort.findById(data.getProductId());

        var config = ProductConfigBuilderSupport.buildProductConfigFromUpdate(data, existing);
        return productConfigRepositoryPort.saveProductConfig(config);
    }

    @Override
    public PolicyProductConfig findById(String id) {
        return productConfigRepositoryPort.findById(id);
    }

    @Override
    public PolicyProductConfig findByProductId(String productId) {
        return productConfigRepositoryPort.findByProductId(productId);
    }

    @Override
    public List<PolicyProductConfig> findAllByPolicyType(PolicyType policyType) {
        return productConfigRepositoryPort.findAllByPolicyType(policyType);
    }

    @Override
    public PolicyRequiredDocument addRequiredDocument(AddRequiredDocumentData data) {
        log.info("Adding required document {} to productConfigId={}", data.getDocumentType(), data.getProductConfigId());

        productConfigSupport.validateAddRequiredDocument(data);
        productConfigRepositoryPort.findById(data.getProductConfigId());

        if (productConfigRepositoryPort.existsRequiredDocument(data.getProductConfigId(), data.getDocumentType())) {
            throw new FunctionalError("Required document " + data.getDocumentType() + " already exists for this product config");
        }

        var doc = ProductConfigBuilderSupport.buildRequiredDocument(data);
        return productConfigRepositoryPort.saveRequiredDocument(doc);
    }

    @Override
    public void removeRequiredDocument(RemoveRequiredDocumentData data) {
        log.info("Removing required document {} from productConfigId={}", data.getDocumentType(), data.getProductConfigId());

        productConfigSupport.validateRemoveRequiredDocument(data);
        productConfigRepositoryPort.findById(data.getProductConfigId());
        productConfigRepositoryPort.deleteRequiredDocument(data.getProductConfigId(), data.getDocumentType());
    }

    @Override
    public List<PolicyRequiredDocument> getRequiredDocuments(String productConfigId) {
        return productConfigRepositoryPort.findRequiredDocuments(productConfigId);
    }
}
