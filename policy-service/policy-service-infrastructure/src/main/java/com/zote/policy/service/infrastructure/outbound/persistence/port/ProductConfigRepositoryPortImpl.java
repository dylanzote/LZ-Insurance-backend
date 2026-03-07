package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import com.zote.policy.service.domain.ports.outbound.ProductConfigRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyProductConfigEntity;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyRequiredDocumentEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyProductConfigRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyRequiredDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductConfigRepositoryPortImpl implements ProductConfigRepositoryPort {

    private final PolicyProductConfigRepository policyProductConfigRepository;
    private final PolicyRequiredDocumentRepository policyRequiredDocumentRepository;
    @Override
    public PolicyProductConfig saveProductConfig(PolicyProductConfig config) {
        log.info("Saving policy product config {}", config);
        return policyProductConfigRepository.save(PolicyProductConfigEntity.toEntity(config)).toDto();
    }

    @Override
    public PolicyProductConfig findById(String id) {
        log.info("Getting product config by id {}", id);
        return policyProductConfigRepository.findById(id)
                .map(PolicyProductConfigEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find product config with id " + id));
    }

    @Override
    public PolicyProductConfig findByProductId(String productId) {
        log.info("Getting product config by productId {}", productId);
        return policyProductConfigRepository.findByProductId(productId)
                .map(PolicyProductConfigEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find product config with productId " + productId));
    }

    @Override
    public List<PolicyProductConfig> findAllByPolicyType(PolicyType policyType) {
        log.info("Getting product configs by policyType {}", policyType);
        return policyProductConfigRepository.findAllByPolicyType(policyType)
                .stream()
                .map(PolicyProductConfigEntity::toDto)
                .toList();
    }

    @Override
    public boolean existsByProductId(String productId) {
        log.info("Checking if product config exists by productId {}", productId);
        return policyProductConfigRepository.existsByProductId(productId);
    }

    @Override
    public PolicyRequiredDocument saveRequiredDocument(PolicyRequiredDocument document) {
        log.info("Saving required document {}", document);
        return policyRequiredDocumentRepository.save(PolicyRequiredDocumentEntity.toEntity(document)).toDto();
    }

    @Override
    public List<PolicyRequiredDocument> findRequiredDocuments(String productConfigId) {
        log.info("Getting required documents for productConfigId {}", productConfigId);
        return policyRequiredDocumentRepository.findAllByProductConfigId(productConfigId)
                .stream()
                .map(PolicyRequiredDocumentEntity::toDto)
                .toList();
    }

    @Override
    public List<PolicyRequiredDocument> findMandatoryRequiredDocuments(String productConfigId) {
        log.info("Getting mandatory required documents for productConfigId {}", productConfigId);
        return policyRequiredDocumentRepository.findAllByProductConfigIdAndMandatoryTrue(productConfigId)
                .stream()
                .map(PolicyRequiredDocumentEntity::toDto)
                .toList();
    }

    @Override
    public boolean existsRequiredDocument(String productConfigId, RequiredDocumentType documentType) {
        log.info("Checking required document {} for productConfigId {}", documentType, productConfigId);
        return policyRequiredDocumentRepository.existsByProductConfigIdAndDocumentType(productConfigId, documentType);
    }
}
