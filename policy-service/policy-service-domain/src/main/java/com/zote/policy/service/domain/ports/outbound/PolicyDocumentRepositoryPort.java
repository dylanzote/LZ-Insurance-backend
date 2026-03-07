package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PolicyDocumentRepositoryPort {

    PolicyDocument savePolicyDocument(PolicyDocument document);

    PolicyDocument findById(String id);

    void deleteById(String id);

    Page<PolicyDocument> findAllByPolicyId(String policyId, Pageable pageable);

    List<PolicyDocument> findAllByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status);

    PolicyDocument findByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name);

    boolean existsByPolicyIdAndTypeAndName(String policyId, PolicyDocumentType type, String name);

    long countByPolicyIdAndStatus(String policyId, PolicyDocumentStatus status);

    boolean hasAllRequiredDocumentsVerified(String policyId, List<String> requiredDocTypes);
}
