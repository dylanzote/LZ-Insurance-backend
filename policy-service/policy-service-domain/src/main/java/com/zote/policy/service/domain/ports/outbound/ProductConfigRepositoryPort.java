package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;

import java.util.List;

public interface ProductConfigRepositoryPort {
    PolicyProductConfig saveProductConfig(PolicyProductConfig config);

    PolicyProductConfig findById(String id);

    PolicyProductConfig findByProductId(String productId);

    List<PolicyProductConfig> findAllByPolicyType(PolicyType policyType);

    boolean existsByProductId(String productId);

    PolicyRequiredDocument saveRequiredDocument(PolicyRequiredDocument document);

    List<PolicyRequiredDocument> findRequiredDocuments(String productConfigId);

    List<PolicyRequiredDocument> findMandatoryRequiredDocuments(String productConfigId);

    boolean existsRequiredDocument(String productConfigId, com.zote.policy.service.domain.enums.RequiredDocumentType documentType);
}
