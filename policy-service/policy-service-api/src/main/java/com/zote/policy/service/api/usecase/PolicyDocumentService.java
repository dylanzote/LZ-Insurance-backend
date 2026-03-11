package com.zote.policy.service.api.usecase;

import com.zote.policy.service.api.controller.PolicyDocumentApi;
import com.zote.policy.service.api.request.*;
import com.zote.policy.service.api.response.PolicyDocumentResponse;
import com.zote.policy.service.domain.models.PolicyDocument;
import com.zote.policy.service.domain.ports.inbound.PolicyDocumentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyDocumentService implements PolicyDocumentApi {

    private final PolicyDocumentPort policyDocumentPort;

    @Override
    public PolicyDocumentResponse uploadDocument(UploadDocumentRequest request) {
        log.info("Uploading document for policy: {}", request.getPolicyId());
        return PolicyDocumentResponse.from(policyDocumentPort.uploadDocument(request.toData()));
    }

    @Override
    public PolicyDocumentResponse verifyDocument(VerifyDocumentRequest request) {
        log.info("Verifying document: {}", request.getDocumentId());
        return PolicyDocumentResponse.from(policyDocumentPort.verifyDocument(request.toData()));
    }

    @Override
    public PolicyDocumentResponse rejectDocument(RejectDocumentRequest request) {
        log.info("Rejecting document: {}", request.getDocumentId());
        return PolicyDocumentResponse.from(policyDocumentPort.rejectDocument(request.toData()));
    }

    @Override
    public List<PolicyDocumentResponse> getDocumentsByPolicyId(String policyId) {
        log.info("Getting documents for policy: {}", policyId);
        return policyDocumentPort.getDocumentsByPolicyId(policyId).stream()
                .map(PolicyDocumentResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasAllRequiredDocumentsVerified(String policyId) {
        log.info("Checking required documents for policy: {}", policyId);
        return policyDocumentPort.hasAllRequiredDocumentsVerified(policyId);
    }

    @Override
    public void deleteDocument(String documentId) {
        log.info("Deleting document: {}", documentId);
        policyDocumentPort.deleteDocument(documentId);
    }

    @Override
    public PolicyDocumentResponse replaceDocument(ReplaceDocumentRequest request) {
        log.info("Replacing document: {}", request.getDocumentId());
        return PolicyDocumentResponse.from(policyDocumentPort.replaceDocument(request.toData()));
    }

    @Override
    public PolicyDocumentResponse getDocumentById(String documentId) {
        log.info("Getting document: {}", documentId);
        return PolicyDocumentResponse.from(policyDocumentPort.getDocumentById(documentId));
    }
}
