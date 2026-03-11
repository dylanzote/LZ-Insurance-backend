package com.zote.policy.service.infrastructure.config;

import com.zote.policy.service.domain.enums.PolicyType;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Configurable defaults for policy creation, renewal, and formatting.
 * All values can be overridden via application.yml.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "policy.defaults")
public class PolicyDefaultsConfig {

    /** Default currency (e.g. XAF, USD). */
    private String currency = "XAF";

    /** Default timezone for policy dates (e.g. Africa/Douala). */
    private String timezone = "Africa/Douala";

    /** Default tax rate for renewal premium calculation (e.g. 1.1925 for 19.25% VAT). */
    private BigDecimal taxRate = new BigDecimal("1.1925");

    /** Number of days a renewal quote remains valid. */
    private int renewalQuoteValidityDays = 30;

    /** Number of days a new quote remains valid for acceptance. */
    private int quoteValidityDays = 30;

    /** Max retries when generating unique policy number. */
    private int policyNumberMaxRetries = 5;

    /** Policy number prefix by policy type (AUTO, HOME, LIFE, etc.). */
    private Map<String, String> policyNumberPrefixes = Map.of(
            "AUTO", "AUT",
            "HOME", "HOM",
            "LIFE", "LIF",
            "HEALTH", "HEA",
            "TRAVEL", "TRV",
            "MOTORCYCLE", "MOT"
    );

    /** Quote number prefix by policy type (e.g. Q-AUT, Q-HOM). */
    private Map<String, String> quoteNumberPrefixes = Map.of(
            "AUTO", "Q-AUT",
            "HOME", "Q-HOM",
            "LIFE", "Q-LIF",
            "HEALTH", "Q-HEA",
            "TRAVEL", "Q-TRV",
            "MOTORCYCLE", "Q-MOT"
    );

    public String getPolicyNumberPrefix(PolicyType type) {
        return policyNumberPrefixes.getOrDefault(type.name(), "POL");
    }

    public String getQuoteNumberPrefix(PolicyType type) {
        return quoteNumberPrefixes.getOrDefault(type.name(), "Q-POL");
    }

    /** Prefix for renewal quote numbers (e.g. REN-). */
    private String renewalQuoteNumberPrefix = "REN";

    /** Default days ahead for renewal reminder queries. */
    private int renewalReminderDaysAhead = 30;

    /** Default grace period (days) before auto-cancellation for non-payment. */
    private int defaultGracePeriodDays = 30;

    /** Batch size for automation jobs (13.5) - renewals, cancellations, overdue detection. */
    private int automationBatchSize = 100;

    public int getDefaultGracePeriodDays() {
        return defaultGracePeriodDays;
    }

    public int getRenewalReminderDaysAhead() {
        return renewalReminderDaysAhead;
    }

    public String getRenewalQuoteNumberPrefix() {
        return renewalQuoteNumberPrefix != null ? renewalQuoteNumberPrefix : "REN";
    }

    public int getAutomationBatchSize() {
        return Math.max(1, automationBatchSize);
    }
}
