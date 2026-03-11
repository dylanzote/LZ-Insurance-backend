package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.AcceptRenewalData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptRenewalRequest {

    @NotBlank
    private String renewalQuoteId;

    private String acceptedBy;

    public AcceptRenewalData toData() {
        return AcceptRenewalData.builder()
                .renewalQuoteId(renewalQuoteId)
                .acceptedBy(acceptedBy)
                .build();
    }
}
