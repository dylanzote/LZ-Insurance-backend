package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.AcceptQuoteData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptQuoteRequest {

    @NotBlank
    private String quoteId;

    private String acceptedBy;

    public AcceptQuoteData toData() {
        return AcceptQuoteData.builder()
                .quoteId(quoteId)
                .acceptedBy(acceptedBy)
                .build();
    }
}
