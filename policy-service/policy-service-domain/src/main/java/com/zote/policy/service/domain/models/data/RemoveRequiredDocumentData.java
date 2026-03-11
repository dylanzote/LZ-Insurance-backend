package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveRequiredDocumentData {
    private String productConfigId;
    private RequiredDocumentType documentType;
}
