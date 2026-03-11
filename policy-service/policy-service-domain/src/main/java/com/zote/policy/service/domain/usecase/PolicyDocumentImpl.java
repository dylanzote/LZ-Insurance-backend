package com.zote.policy.service.domain.usecase;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.models.data.*;
import com.zote.policy.service.domain.ports.inbound.PolicyDocumentPort;
import com.zote.policy.service.domain.ports.outbound.*;
import com.zote.policy.service.domain.support.DocumentBuilderSupport;
import com.zote.policy.service.domain.support.MessagingSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PolicyDocumentImpl implements PolicyDocumentPort {

    private final PolicyDocumentRepositoryPort policyDocumentRepositoryPort;
    private final PolicyRepositoryPort policyRepositoryPort;
    private final ProductConfigRepositoryPort productConfigRepositoryPort;
    private final MessagingSupport messagingSupport;

    @Override
    public PolicyDocument uploadDocument(UploadDocumentData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (data.getType() == null) {
            throw new FunctionalError("document type is required");
        }
        if (data.getName() == null || data.getName().isBlank()) {
            throw new FunctionalError("document name is required");
        }
        if (data.getUrl() == null || data.getUrl().isBlank()) {
            throw new FunctionalError("document url is required");
        }
        policyRepositoryPort.findById(data.getPolicyId());

        var doc = DocumentBuilderSupport.buildDocument(
                data.getPolicyId(),
                data.getType(),
                data.getName(),
                data.getUrl()
        );
        doc.setCreatedBy(data.getUploadedBy());
        doc = policyDocumentRepositoryPort.savePolicyDocument(doc);
        var policy = policyRepositoryPort.findById(data.getPolicyId());
        messagingSupport.publishDocumentUploadedEvent(policy, doc);
        log.info("Uploaded document {} for policy {}", doc.getId(), data.getPolicyId());
        return doc;
    }

    @Override
    public PolicyDocument verifyDocument(VerifyDocumentData data) {
        var doc = policyDocumentRepositoryPort.findById(data.getDocumentId());
        if (doc.getStatus() == PolicyDocumentStatus.VERIFIED) {
            throw new FunctionalError("Document is already verified");
        }
        if (doc.getStatus() == PolicyDocumentStatus.REJECTED) {
            throw new FunctionalError("Rejected documents cannot be verified; replace first");
        }
        doc.setStatus(PolicyDocumentStatus.VERIFIED);
        doc.setVerifiedAt(LocalDateTime.now());
        doc.setVerifiedBy(data.getVerifiedBy());
        doc.setRejectionReason(null);
        doc = policyDocumentRepositoryPort.savePolicyDocument(doc);
        var policy = policyRepositoryPort.findById(doc.getPolicyId());
        messagingSupport.publishDocumentVerifiedEvent(policy, doc);
        log.info("Verified document {} for policy {}", doc.getId(), doc.getPolicyId());
        return doc;
    }

    @Override
    public PolicyDocument rejectDocument(RejectDocumentData data) {
        var doc = policyDocumentRepositoryPort.findById(data.getDocumentId());
        if (doc.getStatus() == PolicyDocumentStatus.VERIFIED) {
            throw new FunctionalError("Verified documents cannot be rejected; replace if incorrect");
        }
        doc.setStatus(PolicyDocumentStatus.REJECTED);
        doc.setVerifiedAt(null);
        doc.setVerifiedBy(null);
        doc.setRejectionReason(data.getRejectionReason());
        doc = policyDocumentRepositoryPort.savePolicyDocument(doc);
        var policy = policyRepositoryPort.findById(doc.getPolicyId());
        messagingSupport.publishDocumentRejectedEvent(policy, doc);
        log.info("Rejected document {} for policy {}", doc.getId(), doc.getPolicyId());
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyDocument> getDocumentsByPolicyId(String policyId) {
        return policyDocumentRepositoryPort.findAllByPolicyId(policyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllRequiredDocumentsVerified(String policyId) {
        var policy = policyRepositoryPort.findById(policyId);
        var productConfig = productConfigRepositoryPort.findById(policy.getProductConfigId());
        var requiredTypes = productConfigRepositoryPort
                .findMandatoryRequiredDocuments(productConfig.getId())
                .stream()
                .map(rd -> com.zote.policy.service.domain.enums.PolicyDocumentType.valueOf(rd.getDocumentType().name()))
                .toList();
        return policyDocumentRepositoryPort.hasAllRequiredDocumentsVerified(policyId, requiredTypes);
    }

    @Override
    public void deleteDocument(String documentId) {
        var doc = policyDocumentRepositoryPort.findById(documentId);
        policyDocumentRepositoryPort.deleteById(documentId);
        var policy = policyRepositoryPort.findById(doc.getPolicyId());
        messagingSupport.publishDocumentDeletedEvent(policy, doc);
        log.info("Deleted document {} for policy {}", documentId, doc.getPolicyId());
    }

    @Override
    public PolicyDocument replaceDocument(ReplaceDocumentData data) {
        if (data.getUrl() == null || data.getUrl().isBlank()) {
            throw new FunctionalError("url is required when replacing document");
        }
        var existing = policyDocumentRepositoryPort.findById(data.getDocumentId());
        var policyId = existing.getPolicyId();
        policyDocumentRepositoryPort.deleteById(data.getDocumentId());

        var doc = DocumentBuilderSupport.buildDocument(
                policyId,
                data.getType() != null ? data.getType() : existing.getType(),
                data.getName() != null ? data.getName() : existing.getName() + "-replaced",
                data.getUrl()
        );
        doc.setCreatedBy(data.getReplacedBy());
        doc = policyDocumentRepositoryPort.savePolicyDocument(doc);
        var policy = policyRepositoryPort.findById(policyId);
        messagingSupport.publishDocumentUploadedEvent(policy, doc);
        log.info("Replaced document {} with new document {} for policy {}", data.getDocumentId(), doc.getId(), policyId);
        return doc;
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyDocument getDocumentById(String documentId) {
        return policyDocumentRepositoryPort.findById(documentId);
    }
}
