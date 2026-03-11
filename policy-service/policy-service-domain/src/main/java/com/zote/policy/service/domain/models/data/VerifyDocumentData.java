package com.zote.policy.service.domain.models.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDocumentData {
    private String documentId;
    private String verifiedBy;
}
