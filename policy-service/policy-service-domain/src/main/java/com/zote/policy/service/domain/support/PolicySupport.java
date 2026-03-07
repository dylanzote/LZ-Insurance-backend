package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.data.CreatePolicyData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PolicySupport {

    public void validateCreatePolicy(CreatePolicyData d) {
        log.info("Validating CreatePolicyData: {}", d);
        if (d.getPremiumTotal() == null || d.getPremiumTotal().signum() <= 0) {
            throw new FunctionalError("premiumTotal must be > 0");
        }
        if (d.getEffectiveDate() == null || d.getExpiryDate() == null) {
            throw new FunctionalError("effectiveDate and expiryDate are required");
        }
        if (!d.getExpiryDate().isAfter(d.getEffectiveDate())) {
            throw new FunctionalError("expiryDate must be after effectiveDate");
        }
        if (d.getType() == null) {
            throw new FunctionalError("policy type is required");
        }
        if (d.getSnapshot() == null || d.getSnapshot().isEmpty()) {
            // You *can* allow empty snapshot, but versioning works best with snapshot.
            log.warn("CreatePolicyData.snapshot is empty (version snapshot will be minimal)");
        }
    }

    public void validatingAllowedPolicyStatus(PolicyStatus status) {
        if (!(status == PolicyStatus.PENDING
                || status == PolicyStatus.PENDING_DOCUMENTS
                || status == PolicyStatus.PENDING_PAYMENT
                || status == PolicyStatus.UNDER_REVIEW
                || status == PolicyStatus.DRAFT)) {
            throw new FunctionalError("Policy cannot be issued from status: " + status);
        }
    }

}
