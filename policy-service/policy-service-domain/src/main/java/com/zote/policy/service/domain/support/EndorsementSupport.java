package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.models.data.EndorsePolicyData;
import com.zote.policy.service.domain.models.data.RequestEndorsementData;
import org.springframework.stereotype.Component;

@Component
public class EndorsementSupport {

    /**
     * Validates endorse policy data.
     * @throws FunctionalError if data is invalid
     */
    public void validateEndorsePolicy(EndorsePolicyData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (data.getChanges() == null || data.getChanges().isEmpty()) {
            throw new FunctionalError("changes are required for endorsement");
        }
    }

    /**
     * Validates request endorsement data.
     * @throws FunctionalError if data is invalid
     */
    public void validateRequestEndorsement(RequestEndorsementData data) {
        if (data.getPolicyId() == null || data.getPolicyId().isBlank()) {
            throw new FunctionalError("policyId is required");
        }
        if (data.getChanges() == null || data.getChanges().isEmpty()) {
            throw new FunctionalError("changes are required for endorsement request");
        }
    }
}
