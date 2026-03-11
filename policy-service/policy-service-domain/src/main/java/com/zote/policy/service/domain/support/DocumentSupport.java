package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.PolicyDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentSupport {

    /**
     * Validates policy document before save.
     */
    public void validatePolicyDocument(PolicyDocument document) {
        if (document == null) {
            throw new FunctionalError("document cannot be null");
        }
        if (document.getPolicyId() == null || document.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (document.getType() == null) {
            throw new FunctionalError("document type is required");
        }
        if (document.getName() == null || document.getName().isBlank()) {
            throw new FunctionalError("document name is required");
        }
        if (document.getUrl() == null || document.getUrl().isBlank()) {
            throw new FunctionalError("document url is required");
        }
    }
}
