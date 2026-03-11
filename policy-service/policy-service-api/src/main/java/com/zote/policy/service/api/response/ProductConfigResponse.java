package com.zote.policy.service.api.response;

import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
public class ProductConfigResponse {

    private String id;
    private String productId;
    private PolicyType policyType;
    private PolicyTermUnit termUnit;
    private Integer termLength;
    private BillingPlan defaultBillingPlan;
    private boolean allowFullPayment;
    private boolean allowInstallments;
    private Integer installmentsCount;
    private boolean requireDocuments;
    private boolean requiresUnderwriting;
    private String createdBy;
    private LocalDateTime createdAt;
    private String lastModifiedBy;
    private LocalDateTime updatedAt;

    public static ProductConfigResponse from(PolicyProductConfig config) {
        ProductConfigResponse response = new ProductConfigResponse();
        BeanUtils.copyProperties(config, response);
        return response;
    }
}
