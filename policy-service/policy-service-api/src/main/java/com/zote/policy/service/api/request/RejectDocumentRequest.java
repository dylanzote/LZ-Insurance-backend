package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.RejectDocumentData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RejectDocumentRequest {

    @NotBlank
    private String documentId;

    private String rejectedBy;

    @NotBlank
    private String rejectionReason;

    public RejectDocumentData toData() {
        return RejectDocumentData.builder()
                .documentId(documentId)
                .rejectedBy(rejectedBy)
                .rejectionReason(rejectionReason)
                .build();
    }
}
