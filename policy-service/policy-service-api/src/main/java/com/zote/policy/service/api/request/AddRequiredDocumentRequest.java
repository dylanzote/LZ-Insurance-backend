package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.domain.models.data.AddRequiredDocumentData;
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
public class AddRequiredDocumentRequest {

    @NotBlank
    private String productConfigId;

    @NotNull
    private RequiredDocumentType documentType;

    private boolean mandatory;

    private String createdBy;

    public AddRequiredDocumentData toData() {
        return AddRequiredDocumentData.builder()
                .productConfigId(productConfigId)
                .documentType(documentType)
                .mandatory(mandatory)
                .createdBy(createdBy)
                .build();
    }
}
