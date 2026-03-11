package com.zote.policy.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.PolicyTermUnit;
import com.zote.policy.service.domain.models.BillingPlan;
import com.zote.policy.service.domain.models.Policy;
import com.zote.policy.service.domain.models.PolicyProductConfig;
import com.zote.policy.service.domain.models.Quote;

import java.math.BigDecimal;
import com.zote.policy.service.domain.models.data.AddRequiredDocumentData;
import com.zote.policy.service.domain.models.data.CreateProductConfigData;
import com.zote.policy.service.domain.models.data.CreateQuoteData;
import com.zote.policy.service.domain.models.data.RemoveRequiredDocumentData;
import com.zote.policy.service.domain.models.data.UpdateProductConfigData;
import org.springframework.stereotype.Component;

@Component
public class ProductConfigSupport {

    public void validateCreateProductConfig(CreateProductConfigData data) {
        if (data.getProductId() == null || data.getProductId().isBlank()) {
            throw new FunctionalError("productId is required");
        }
        if (data.getPolicyType() == null) {
            throw new FunctionalError("policyType is required");
        }
        if (data.getTermUnit() == null) {
            throw new FunctionalError("termUnit is required");
        }
        if (data.getDefaultBillingPlan() == null) {
            throw new FunctionalError("defaultBillingPlan is required");
        }
        validateBillingPlanSettings(data.getDefaultBillingPlan(), data.isAllowFullPayment(),
                data.isAllowInstallments(), data.getInstallmentsCount());
    }

    public void validateUpdateProductConfig(UpdateProductConfigData data) {
        if (data.getId() == null || data.getId().isBlank()) {
            throw new FunctionalError("id is required");
        }
        if (data.getProductId() == null || data.getProductId().isBlank()) {
            throw new FunctionalError("productId is required");
        }
        if (data.getPolicyType() == null) {
            throw new FunctionalError("policyType is required");
        }
        if (data.getTermUnit() == null) {
            throw new FunctionalError("termUnit is required");
        }
        if (data.getDefaultBillingPlan() == null) {
            throw new FunctionalError("defaultBillingPlan is required");
        }
        validateBillingPlanSettings(data.getDefaultBillingPlan(), data.isAllowFullPayment(),
                data.isAllowInstallments(), data.getInstallmentsCount());
    }

    public void validateAddRequiredDocument(AddRequiredDocumentData data) {
        if (data.getProductConfigId() == null || data.getProductConfigId().isBlank()) {
            throw new FunctionalError("productConfigId is required");
        }
        if (data.getDocumentType() == null) {
            throw new FunctionalError("documentType is required");
        }
    }

    public void validateRemoveRequiredDocument(RemoveRequiredDocumentData data) {
        if (data.getProductConfigId() == null || data.getProductConfigId().isBlank()) {
            throw new FunctionalError("productConfigId is required");
        }
        if (data.getDocumentType() == null) {
            throw new FunctionalError("documentType is required");
        }
    }

    /**
     * Validates that the quote's billing plan is allowed by the product config.
     */
    public void validateBillingPlanAgainstConfig(Quote quote, PolicyProductConfig config) {
        validateBillingPlanAllowed(quote.getBillingPlan(), config);
    }

    /**
     * Validates that the requested billing plan is allowed by the product config.
     */
    public void validateBillingPlanAgainstConfig(CreateQuoteData data, PolicyProductConfig config) {
        validateBillingPlanAllowed(data.getBillingPlan(), config);
    }

    private void validateBillingPlanAllowed(BillingPlan requested, PolicyProductConfig config) {
        if (requested == null) {
            return;
        }
        switch (requested) {
            case FULL -> {
                if (!config.isAllowFullPayment()) {
                    throw new FunctionalError("Full payment is not allowed for this product");
                }
            }
            case MONTHLY, QUARTERLY -> {
                if (!config.isAllowInstallments()) {
                    throw new FunctionalError("Installments are not allowed for this product");
                }
                if (config.getInstallmentsCount() == null || config.getInstallmentsCount() <= 0) {
                    throw new FunctionalError("Product does not support installment billing");
                }
            }
        }
    }

    /**
     * Enforces product-specific regulatory constraints (14.2).
     */
    public void validateRegulatoryRules(Policy policy, PolicyProductConfig config) {
        if (policy.getPremiumTotal() != null && config.getMinPremium() != null
                && policy.getPremiumTotal().compareTo(config.getMinPremium()) < 0) {
            throw new FunctionalError("Premium " + policy.getPremiumTotal() + " is below product minimum " + config.getMinPremium());
        }
        if (policy.getPremiumTotal() != null && config.getMaxPremium() != null
                && policy.getPremiumTotal().compareTo(config.getMaxPremium()) > 0) {
            throw new FunctionalError("Premium " + policy.getPremiumTotal() + " exceeds product maximum " + config.getMaxPremium());
        }
        if (policy.getGracePeriodDays() != null && config.getMaxGracePeriodDays() != null
                && policy.getGracePeriodDays() > config.getMaxGracePeriodDays()) {
            throw new FunctionalError("Grace period " + policy.getGracePeriodDays() + " days exceeds product maximum " + config.getMaxGracePeriodDays());
        }
    }

    private void validateBillingPlanSettings(BillingPlan defaultPlan, boolean allowFull, boolean allowInstallments, Integer installmentsCount) {
        if (defaultPlan == BillingPlan.FULL && !allowFull) {
            throw new FunctionalError("defaultBillingPlan is FULL but allowFullPayment is false");
        }
        if ((defaultPlan == BillingPlan.MONTHLY || defaultPlan == BillingPlan.QUARTERLY) && !allowInstallments) {
            throw new FunctionalError("defaultBillingPlan requires installments but allowInstallments is false");
        }
        if (allowInstallments && (installmentsCount == null || installmentsCount <= 0)) {
            throw new FunctionalError("installmentsCount must be positive when allowInstallments is true");
        }
    }
}
