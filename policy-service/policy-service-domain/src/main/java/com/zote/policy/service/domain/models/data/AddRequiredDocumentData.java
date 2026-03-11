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
public class AddRequiredDocumentData {
    private String productConfigId;
    private RequiredDocumentType documentType;
    private boolean mandatory;
    private String createdBy;
}
