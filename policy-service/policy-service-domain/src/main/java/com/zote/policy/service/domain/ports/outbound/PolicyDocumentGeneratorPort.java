package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyDocument;

/**
 * Port for generating policy documents (e.g. policy schedule PDF).
 * Implemented in infrastructure.
 */
public interface PolicyDocumentGeneratorPort {

    /**
     * Generates the policy schedule document (PDF) and returns the document metadata
     * with storage URL. The implementation handles PDF generation and storage.
     */
    PolicyDocument generatePolicyScheduleDocument(Policy policy);
}
