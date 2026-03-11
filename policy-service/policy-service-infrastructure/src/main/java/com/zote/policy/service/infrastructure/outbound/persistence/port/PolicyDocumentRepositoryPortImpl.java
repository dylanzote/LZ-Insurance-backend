package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.ports.outbound.PolicyDocumentRepositoryPort;
import com.zote.policy.service.domain.support.DocumentSupport;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyDocumentEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyDocumentRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PolicyDocumentRepositoryPortImpl implements PolicyDocumentRepositoryPort {

    private final PolicyDocumentRepository policyDocumentRepository;
    private final PolicyRepository policyRepository;
    private final DocumentSupport documentSupport;

    @Override
    public PolicyDocument savePolicyDocument(PolicyDocument document) {
        documentSupport.validatePolicyDocument(document);
        log.info("Saving policy document {}", document);
        var entity = PolicyDocumentEntity.toEntity(document);
        if (document.getPolicyId() != null && entity.getPolicy() == null) {
            entity.setPolicy(policyRepository.getReferenceById(document.getPolicyId()));
        }
        return policyDocumentRepository.save(entity).toDto();
    }

    @Override
    public PolicyDocument findById(String id) {
        log.info("Getting policy document with id {}", id);
        return policyDocumentRepository.findById(id)
                .map(PolicyDocumentEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find policy document with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting policy document with id {}", id);
        policyDocumentRepository.deleteById(id);
    }

    @Override
    public Page<PolicyDocument> findAllByPolicyId(String policyId, Pageable pageable) {
        log.info("Getting policy documents by policyId {}", policyId);
        return policyDocumentRepository.findAllByPolicyId(policyId, pageable)
                .map(PolicyDocumentEntity::toDto);
    }

    @Override
    public List<PolicyDocument> findAllByPolicyId(String policyId) {
        return findAllByPolicyId(policyId, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Override
    public List<PolicyDocument> findAllByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status) {
        log.info("Getting policy documents by policyId {} and status {}", policyId, status);
        return policyDocumentRepository.findAllByPolicyIdAndStatus(policyId, status)
                .stream()
                .map(PolicyDocumentEntity::toDto)
                .toList();
    }

    @Override
    public PolicyDocument findByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name) {
        log.info("Getting policy document by policyId {}, type {}, name {}", policyId, type, name);
        return policyDocumentRepository.findByPolicyIdAndTypeAndName(policyId, type, name)
                .map(PolicyDocumentEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find policy document for policyId " + policyId + " type " + type + " name " + name));
    }

    @Override
    public boolean existsByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name) {
        log.info("Checking if policy document exists by policyId {}, type {}, name {}", policyId, type, name);
        return policyDocumentRepository.existsByPolicyIdAndTypeAndName(policyId, type, name);
    }

    @Override
    public long countByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status) {
        log.info("Counting policy documents by policyId {} and status {}", policyId, status);
        return policyDocumentRepository.countByPolicyIdAndStatus(policyId, status);
    }

    @Override
    public boolean hasAllRequiredDocumentsVerified(String policyId, List<PolicyDocumentType> requiredDocTypes) {
        log.info("Checking required verified documents for policyId {}", policyId);

        var verifiedDocs = policyDocumentRepository.findAllByPolicyIdAndStatus(policyId, PolicyDocumentStatus.VERIFIED)
                .stream()
                .map(PolicyDocumentEntity::toDto)
                .map(doc -> doc.getType().name())
                .collect(Collectors.toSet());

        return requiredDocTypes.stream().allMatch(verifiedDocs::contains);
    }
}
