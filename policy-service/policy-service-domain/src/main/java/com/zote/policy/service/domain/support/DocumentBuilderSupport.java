package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.PolicyDocumentStatus;
import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.PolicyDocument;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Centralized builders for PolicyDocument.
 */
@UtilityClass
public class DocumentBuilderSupport {

    /**
     * Builds a PolicyDocument with required fields.
     */
    public PolicyDocument buildDocument(String policyId, PolicyDocumentType type, String name, String url) {
        return PolicyDocument.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .type(type)
                .name(name)
                .url(url)
                .status(PolicyDocumentStatus.UPLOADED)
                .build();
    }

    /**
     * Builds a system-generated policy schedule document (VERIFIED status).
     */
    public PolicyDocument buildGeneratedPolicyScheduleDocument(String policyId, String name, String url) {
        return PolicyDocument.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .type(PolicyDocumentType.POLICY_SCHEDULE)
                .name(name)
                .url(url)
                .status(PolicyDocumentStatus.VERIFIED)
                .build();
    }
}
