package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.Endorsement;
import com.zote.policy.service.domain.models.data.EndorsePolicyData;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Builder support for Endorsement domain entity.
 * Centralizes construction logic per project rules.
 */
@UtilityClass
public class EndorsementBuilderSupport {

    public Endorsement buildFromEndorseData(EndorsePolicyData data, String policyId, BigDecimal premiumChange) {
        EndorsementType type = data.getType() != null ? data.getType() : EndorsementType.OTHER;
        String description = data.getDescription() != null && !data.getDescription().isBlank()
                ? data.getDescription()
                : "Policy endorsement";
        return Endorsement.builder()
                .id(UUID.randomUUID().toString())
                .policyId(policyId)
                .type(type)
                .description(description)
                .effectiveDate(LocalDate.now())
                .premiumChange(premiumChange)
                .changes(data.getChanges())
                .createdBy(data.getEndorsedBy())
                .build();
    }
}
