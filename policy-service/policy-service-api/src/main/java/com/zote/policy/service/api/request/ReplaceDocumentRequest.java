package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.data.ReplaceDocumentData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplaceDocumentRequest {

    @NotBlank
    private String documentId;

    private String name;

    @NotBlank
    private String url;

    private PolicyDocumentType type;

    private String replacedBy;

    public ReplaceDocumentData toData() {
        return ReplaceDocumentData.builder()
                .documentId(documentId)
                .name(name)
                .url(url)
                .type(type)
                .replacedBy(replacedBy)
                .build();
    }
}
