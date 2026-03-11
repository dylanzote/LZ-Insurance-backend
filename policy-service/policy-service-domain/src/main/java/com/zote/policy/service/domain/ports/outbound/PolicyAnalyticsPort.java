package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.analytics.*;

import java.time.LocalDate;

/**
 * Port for portfolio analytics queries (Epic 16).
 */
public interface PolicyAnalyticsPort {

    /** 16.1 - Portfolio snapshot */
    PortfolioSnapshot getPortfolioSnapshot();

    /** 16.2 - Policy KPIs */
    PolicyKpis getPolicyKpis();

    /** 16.3 - Renewal rates over time */
    RenewalRatesSummary getRenewalRates(LocalDate from, LocalDate to, String periodType);

    /** 16.4 - Cancellation rates */
    CancellationRatesSummary getCancellationRates(LocalDate from, LocalDate to, String periodType);

    /** 16.5 - Premium summary by product, branch, or agent */
    PremiumSummary getPremiumSummary(String dimension);

    /** 16.6 - Policy trends (new, renewals, cancellations) over time */
    PolicyTrends getPolicyTrends(LocalDate from, LocalDate to, String periodType);
}
