package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.PolicyDocumentType;
import com.zote.policy.service.domain.models.data.UploadDocumentData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadDocumentRequest {

    @NotBlank
    private String policyId;

    @NotNull
    private PolicyDocumentType type;

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    private String uploadedBy;

    public UploadDocumentData toData() {
        return UploadDocumentData.builder()
                .policyId(policyId)
                .type(type)
                .name(name)
                .url(url)
                .uploadedBy(uploadedBy)
                .build();
    }
}
