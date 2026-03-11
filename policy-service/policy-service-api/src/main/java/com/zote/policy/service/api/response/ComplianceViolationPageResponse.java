package com.zote.policy.service.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
public class ComplianceViolationPageResponse {

    private List<ComplianceViolationResponse> data;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    @JsonProperty("isFirst")
    private boolean isFirst;

    @JsonProperty("isLast")
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public static ComplianceViolationPageResponse from(Page<ComplianceViolationResponse> page) {
        return new ComplianceViolationPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1,
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
