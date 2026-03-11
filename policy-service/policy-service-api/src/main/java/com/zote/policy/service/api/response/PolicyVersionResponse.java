package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyVersionReason;
import com.zote.policy.service.domain.models.PolicyVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyVersionResponse {

    private String id;
    private String policyId;
    private Integer versionNo;
    private PolicyVersionReason reason;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private BigDecimal premiumTotal;
    private String createdBy;
    private LocalDateTime createdAt;

    public static PolicyVersionResponse from(PolicyVersion v) {
        return PolicyVersionResponse.builder()
                .id(v.getId())
                .policyId(v.getPolicyId())
                .versionNo(v.getVersionNo())
                .reason(v.getReason())
                .effectiveFrom(v.getEffectiveFrom())
                .effectiveTo(v.getEffectiveTo())
                .premiumTotal(v.getPremiumTotal())
                .createdBy(v.getCreatedBy())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
