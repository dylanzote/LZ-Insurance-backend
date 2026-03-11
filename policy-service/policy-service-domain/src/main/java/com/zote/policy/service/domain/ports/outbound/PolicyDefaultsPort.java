package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.PolicyType;

import java.math.BigDecimal;

/**
 * Provides configurable defaults for policy creation and renewal.
 * Implemented in infrastructure using application config.
 */
public interface PolicyDefaultsPort {

    String getDefaultCurrency();

    String getDefaultTimezone();

    BigDecimal getDefaultTaxRate();

    int getRenewalQuoteValidityDays();

    /** Number of days a new quote remains valid for acceptance. */
    int getQuoteValidityDays();

    int getPolicyNumberMaxRetries();

    String getPolicyNumberPrefix(PolicyType type);

    /** Prefix for quote numbers by policy type (e.g. Q-AUT, Q-HOM). */
    String getQuoteNumberPrefix(PolicyType type);

    /** Prefix for renewal quote numbers (e.g. REN). */
    String getRenewalQuoteNumberPrefix();

    /** Default days ahead for renewal reminder queries. */
    int getRenewalReminderDaysAhead();

    /** Default grace period (days) before auto-cancellation for non-payment (9.7). */
    int getDefaultGracePeriodDays();

    /** Batch size for automation jobs (13.5). */
    int getAutomationBatchSize();
}
