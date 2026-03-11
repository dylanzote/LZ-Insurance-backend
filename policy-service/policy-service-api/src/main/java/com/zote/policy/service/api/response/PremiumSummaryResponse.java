package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.models.analytics.PremiumSummary;
import com.zote.policy.service.domain.models.analytics.PremiumSummaryRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PremiumSummaryResponse {
    private String dimension;
    private List<PremiumSummaryRowResponse> rows;
    private BigDecimal grandTotal;
    private LocalDateTime asOf;

    public static PremiumSummaryResponse from(PremiumSummary summary) {
        List<PremiumSummaryRowResponse> rowList = summary.getRows() != null
                ? summary.getRows().stream()
                        .map(PremiumSummaryRowResponse::from)
                        .collect(Collectors.toList())
                : List.of();
        return PremiumSummaryResponse.builder()
                .dimension(summary.getDimension())
                .rows(rowList)
                .grandTotal(summary.getGrandTotal())
                .asOf(summary.getAsOf())
                .build();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PremiumSummaryRowResponse {
        private String dimensionValue;
        private long policyCount;
        private BigDecimal totalPremium;

        public static PremiumSummaryRowResponse from(PremiumSummaryRow r) {
            return PremiumSummaryRowResponse.builder()
                    .dimensionValue(r.getDimensionValue())
                    .policyCount(r.getPolicyCount())
                    .totalPremium(r.getTotalPremium())
                    .build();
        }
    }
}
