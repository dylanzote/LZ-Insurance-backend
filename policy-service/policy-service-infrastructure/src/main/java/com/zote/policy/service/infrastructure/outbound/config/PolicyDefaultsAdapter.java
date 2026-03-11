package com.zote.policy.service.infrastructure.outbound.config;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.ports.outbound.PolicyDefaultsPort;
import com.zote.policy.service.infrastructure.config.PolicyDefaultsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Infrastructure adapter that provides policy defaults from application config.
 */
@Component
@RequiredArgsConstructor
public class PolicyDefaultsAdapter implements PolicyDefaultsPort {

    private final PolicyDefaultsConfig config;

    @Override
    public String getDefaultCurrency() {
        return config.getCurrency();
    }

    @Override
    public String getDefaultTimezone() {
        return config.getTimezone();
    }

    @Override
    public BigDecimal getDefaultTaxRate() {
        return config.getTaxRate();
    }

    @Override
    public int getRenewalQuoteValidityDays() {
        return config.getRenewalQuoteValidityDays();
    }

    @Override
    public int getQuoteValidityDays() {
        return config.getQuoteValidityDays();
    }

    @Override
    public int getPolicyNumberMaxRetries() {
        return config.getPolicyNumberMaxRetries();
    }

    @Override
    public String getPolicyNumberPrefix(PolicyType type) {
        return config.getPolicyNumberPrefix(type);
    }

    @Override
    public String getQuoteNumberPrefix(PolicyType type) {
        return config.getQuoteNumberPrefix(type);
    }

    @Override
    public String getRenewalQuoteNumberPrefix() {
        return config.getRenewalQuoteNumberPrefix();
    }

    @Override
    public int getRenewalReminderDaysAhead() {
        return config.getRenewalReminderDaysAhead();
    }

    @Override
    public int getDefaultGracePeriodDays() {
        return config.getDefaultGracePeriodDays();
    }

    @Override
    public int getAutomationBatchSize() {
        return config.getAutomationBatchSize();
    }
}
