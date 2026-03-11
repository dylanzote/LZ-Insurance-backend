package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.PolicyRequiredDocument;
import com.zote.policy.service.domain.models.data.AddRequiredDocumentData;
import com.zote.policy.service.domain.models.data.CreateProductConfigData;
import com.zote.policy.service.domain.models.data.UpdateProductConfigData;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder support for Product Configuration domain entities.
 * Centralizes construction logic for PolicyProductConfig and PolicyRequiredDocument.
 */
@UtilityClass
public class ProductConfigBuilderSupport {

    public PolicyProductConfig buildProductConfigFromCreate(CreateProductConfigData data) {
        var now = LocalDateTime.now();
        return PolicyProductConfig.builder()
                .id(UUID.randomUUID().toString())
                .productId(data.getProductId())
                .policyType(data.getPolicyType())
                .termUnit(data.getTermUnit())
                .termLength(data.getTermLength())
                .defaultBillingPlan(data.getDefaultBillingPlan())
                .allowFullPayment(data.isAllowFullPayment())
                .allowInstallments(data.isAllowInstallments())
                .installmentsCount(data.getInstallmentsCount())
                .requireDocuments(data.isRequireDocuments())
                .requiresUnderwriting(data.isRequiresUnderwriting())
                .createdBy(data.getCreatedBy())
                .createdAt(now)
                .lastModifiedBy(data.getCreatedBy())
                .updatedAt(now)
                .build();
    }

    public PolicyProductConfig buildProductConfigFromUpdate(UpdateProductConfigData data, PolicyProductConfig existing) {
        var now = LocalDateTime.now();
        return PolicyProductConfig.builder()
                .id(data.getId())
                .productId(data.getProductId())
                .policyType(data.getPolicyType())
                .termUnit(data.getTermUnit())
                .termLength(data.getTermLength())
                .defaultBillingPlan(data.getDefaultBillingPlan())
                .allowFullPayment(data.isAllowFullPayment())
                .allowInstallments(data.isAllowInstallments())
                .installmentsCount(data.getInstallmentsCount())
                .requireDocuments(data.isRequireDocuments())
                .requiresUnderwriting(data.isRequiresUnderwriting())
                .createdBy(existing.getCreatedBy())
                .createdAt(existing.getCreatedAt())
                .lastModifiedBy(data.getLastModifiedBy())
                .updatedAt(now)
                .build();
    }

    public PolicyRequiredDocument buildRequiredDocument(AddRequiredDocumentData data) {
        var now = LocalDateTime.now();
        return PolicyRequiredDocument.builder()
                .id(UUID.randomUUID().toString())
                .productConfigId(data.getProductConfigId())
                .documentType(data.getDocumentType())
                .mandatory(data.isMandatory())
                .createdBy(data.getCreatedBy())
                .createdAt(now)
                .lastModifiedBy(data.getCreatedBy())
                .updatedAt(now)
                .build();
    }
}
