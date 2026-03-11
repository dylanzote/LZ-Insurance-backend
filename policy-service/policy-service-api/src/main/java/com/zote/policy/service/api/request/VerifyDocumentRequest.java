package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.VerifyDocumentData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDocumentRequest {

    @NotBlank
    private String documentId;

    private String verifiedBy;

    public VerifyDocumentData toData() {
        return VerifyDocumentData.builder()
                .documentId(documentId)
                .verifiedBy(verifiedBy)
                .build();
    }
}
