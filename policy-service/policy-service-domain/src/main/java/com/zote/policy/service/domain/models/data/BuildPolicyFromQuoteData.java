package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildPolicyFromQuoteData {
    private String policyNumber;
    private PolicyStatus policyStatus;
    private Quote quote;
    private String productConfigId;
    private String currency;
    private String timezone;
}
