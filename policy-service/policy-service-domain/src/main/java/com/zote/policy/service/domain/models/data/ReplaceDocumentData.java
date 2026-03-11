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
public class ReplaceDocumentData {
    private String documentId;
    private String name;
    private String url;
    private PolicyDocumentType type;
    private String replacedBy;
}
