package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.AddUnderwritingNoteData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUnderwritingNoteRequest {

    @NotBlank
    private String quoteId;

    @NotBlank
    private String note;

    private String createdBy;

    public AddUnderwritingNoteData toData() {
        return AddUnderwritingNoteData.builder()
                .quoteId(quoteId)
                .note(note)
                .createdBy(createdBy)
                .build();
    }
}
