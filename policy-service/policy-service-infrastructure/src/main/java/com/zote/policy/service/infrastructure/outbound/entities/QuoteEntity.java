package com.zote.policy.service.infrastructure.outbound.entities;

import com.zote.common.utils.audit.Auditable;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.enums.QuoteStatus;
import com.zote.policy.service.domain.enums.UnderwritingDecisionStatus;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.CoveragePremium;
import com.zote.policy.service.domain.models.Quote;
import com.zote.policy.service.domain.models.SelectedCoverage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "quote")
public class QuoteEntity extends Auditable {

    @Id
    private String id;

    @Column(name = "quote_number", nullable = false, unique = true, length = 64)
    private String quoteNumber;

    @Column(name = "product_id", nullable = false, length = 64)
    private String productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 24)
    private PolicyType policyType;

    @Column(name = "customer_id", nullable = false, length = 64)
    private String customerId;

    @Column(name = "agent_id", length = 64)
    private String agentId;

    @Column(name = "branch_id", length = 64)
    private String branchId;

    @Column(name = "parent_policy_id", length = 64)
    private String parentPolicyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_plan", nullable = false, length = 24)
    private BillingPlan billingPlan;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rating_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> ratingData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "selected_coverages", columnDefinition = "jsonb", nullable = false)
    private List<Map<String, Object>> selectedCoverages;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "coverage_premiums", columnDefinition = "jsonb", nullable = false)
    private List<Map<String, Object>> coveragePremiums;

    @Column(name = "premium_before_tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumBeforeTax;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "premium_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal premiumTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private QuoteStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "underwriting_decision", nullable = false, length = 24)
    private UnderwritingDecisionStatus underwritingDecision;

    @Column(name = "underwriting_reason", length = 1024)
    private String underwritingReason;

    @Column(name = "underwriting_decided_by", length = 64)
    private String underwritingDecidedBy;

    @Column(name = "underwriting_decided_at")
    private LocalDateTime underwritingDecidedAt;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "snapshot", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> snapshot;

    public static QuoteEntity toEntity(Quote quote) {
        QuoteEntity entity = new QuoteEntity();
        BeanUtils.copyProperties(quote, entity);

        entity.setSelectedCoverages(
                quote.getSelectedCoverages() == null ? List.of() :
                        quote.getSelectedCoverages().stream()
                                .map(sc -> Map.<String, Object>of(
                                        "coverageCode", sc.getCoverageCode(),
                                        "limit", sc.getLimit(),
                                        "deductible", sc.getDeductible()
                                )).toList()
        );

        entity.setCoveragePremiums(
                quote.getCoveragePremiums() == null ? List.of() :
                        quote.getCoveragePremiums().stream()
                                .map(cp -> Map.<String, Object>of(
                                        "coverageCode", cp.getCoverageCode(),
                                        "premium", cp.getPremium()
                                )).toList()
        );

        return entity;
    }

    @SuppressWarnings("unchecked")
    public Quote toDto() {
        Quote dto = new Quote();
        BeanUtils.copyProperties(this, dto);

        if (this.selectedCoverages != null) {
            dto.setSelectedCoverages(
                    this.selectedCoverages.stream()
                            .map(map -> SelectedCoverage.builder()
                                    .coverageCode((String) map.get("coverageCode"))
                                    .limit(map.get("limit") == null ? null : new BigDecimal(map.get("limit").toString()))
                                    .deductible(map.get("deductible") == null ? null : new BigDecimal(map.get("deductible").toString()))
                                    .build())
                            .toList()
            );
        }

        if (this.coveragePremiums != null) {
            dto.setCoveragePremiums(
                    this.coveragePremiums.stream()
                            .map(map -> CoveragePremium.builder()
                                    .coverageCode((String) map.get("coverageCode"))
                                    .premium(map.get("premium") == null ? null : new BigDecimal(map.get("premium").toString()))
                                    .build())
                            .toList()
            );
        }

        return dto;
    }
}
