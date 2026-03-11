package com.zote.policy.service.api.request;

import com.zote.policy.service.domain.models.data.ApproveCancellationRequestData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveCancellationRequest {

    @NotBlank
    private String cancellationRequestId;

    private String decidedBy;

    private LocalDate effectiveDate;

    public ApproveCancellationRequestData toData() {
        return ApproveCancellationRequestData.builder()
                .cancellationRequestId(cancellationRequestId)
                .decidedBy(decidedBy)
                .effectiveDate(effectiveDate)
                .build();
    }
}
