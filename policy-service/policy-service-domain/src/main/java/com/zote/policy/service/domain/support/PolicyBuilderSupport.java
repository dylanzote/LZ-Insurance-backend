package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PaymentStatus;
import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.PolicyVersionReason;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyStatusHistory;
import com.zote.policy.service.domain.models.PolicyVersion;
import com.zote.policy.service.domain.models.data.CreatePolicyData;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class PolicyBuilderSupport {

    public Policy policyBuilder(CreatePolicyData data) {
        return Policy.builder()
                .id(UUID.randomUUID().toString())
                .policyNumber(generatePolicyNumber(data.getType()))
                .type(data.getType())
                .productId(required(data.getProductId(), "productId"))
                .customerId(required(data.getCustomerId(), "customerId"))
                .agentId(data.getAgentId())
                .branchId(data.getBranchId())
                .status(PolicyStatus.DRAFT)
                .paymentStatus(PaymentStatus.PENDING)
                .currency("XAF")
                .premiumTotal(data.getPremiumTotal())
                .effectiveDate(data.getEffectiveDate())
                .expiryDate(data.getExpiryDate())
                .build();
    }

    public PolicyVersion policyVersionBuilder(Policy policy, Map<String, Object> snapshot) {
        return PolicyVersion.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policy.getId())
                .versionNo(1)
                .reason(PolicyVersionReason.ISSUANCE)
                .effectiveFrom(policy.getEffectiveDate())
                .effectiveTo(null)
                .premiumTotal(policy.getPremiumTotal())
                .snapshot(snapshot)
                .build();
    }

    public PolicyStatusHistory policyStatusHistoryBuilder(String policyId, PolicyStatus from) {
        return PolicyStatusHistory.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .fromStatus(from)
                .toStatus(PolicyStatus.ACTIVE)
                .reason("issued")
                .build();
    }

    private String generatePolicyNumber(PolicyType type) {
        String prefix = switch (type) {
            case AUTO -> "AUT";
            case HOME -> "HOM";
            case LIFE -> "LIF";
            case HEALTH -> "HEA";
            case TRAVEL -> "TRV";
            case MOTORCYCLE -> "MOT";
        };
        return prefix + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new FunctionalError(field + " is required");
        return value;
    }
}
