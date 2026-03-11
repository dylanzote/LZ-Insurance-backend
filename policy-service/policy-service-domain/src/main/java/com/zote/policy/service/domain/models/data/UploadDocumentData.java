package com.zote.policy.service.domain.models.data;

import com.zote.policy.service.domain.enums.PolicyDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentData {
    private String policyId;
    private PolicyDocumentType type;
    private String name;
    private String url;
    private String uploadedBy;
}
