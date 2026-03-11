package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.data.RemoveRequiredDocumentData;
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
public class RemoveRequiredDocumentRequest {

    @NotBlank
    private String productConfigId;

    @NotNull
    private RequiredDocumentType documentType;

    public RemoveRequiredDocumentData toData() {
        return RemoveRequiredDocumentData.builder()
                .productConfigId(productConfigId)
                .documentType(documentType)
                .build();
    }
}
