package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePolicyFromQuoteData {
    private String quoteId;
    private String createdBy;
}
