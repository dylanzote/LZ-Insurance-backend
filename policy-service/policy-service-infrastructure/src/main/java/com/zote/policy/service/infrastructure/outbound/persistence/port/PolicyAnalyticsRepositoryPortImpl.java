package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.analytics.*;
import com.zote.policy.service.domain.ports.outbound.PolicyAnalyticsPort;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyRepository;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyAnalyticsRepositoryPortImpl implements PolicyAnalyticsPort {

    private final PolicyRepository policyRepository;
    private final PolicyStatusHistoryRepository statusHistoryRepository;

    @Override
    public PortfolioSnapshot getPortfolioSnapshot() {
        var countByStatus = policyRepository.countGroupByStatus().stream()
                .collect(Collectors.toMap(row -> ((PolicyStatus) row[0]).name(), row -> (Long) row[1]));

        long active = countByStatus.getOrDefault(PolicyStatus.ACTIVE.name(), 0L);
        long expired = countByStatus.getOrDefault(PolicyStatus.EXPIRED.name(), 0L);
        long cancelled = countByStatus.getOrDefault(PolicyStatus.CANCELLED.name(), 0L);
        long suspended = countByStatus.getOrDefault(PolicyStatus.SUSPENDED.name(), 0L);
        long pending = countByStatus.getOrDefault(PolicyStatus.PENDING.name(), 0L)
                + countByStatus.getOrDefault(PolicyStatus.PENDING_PAYMENT.name(), 0L)
                + countByStatus.getOrDefault(PolicyStatus.PENDING_DOCUMENTS.name(), 0L)
                + countByStatus.getOrDefault(PolicyStatus.UNDER_REVIEW.name(), 0L);

        BigDecimal totalPremium = policyRepository.sumPremiumActive();

        return PortfolioSnapshot.builder()
                .totalPolicies(countByStatus.values().stream().mapToLong(Long::longValue).sum())
                .activePolicies(active)
                .expiredPolicies(expired)
                .cancelledPolicies(cancelled)
                .suspendedPolicies(suspended)
                .pendingPolicies(pending)
                .countByStatus(countByStatus.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue())))
                .totalPremium(totalPremium != null ? totalPremium : BigDecimal.ZERO)
                .asOf(LocalDateTime.now())
                .build();
    }

    @Override
    public PolicyKpis getPolicyKpis() {
        long active = policyRepository.countByStatus(PolicyStatus.ACTIVE);
        BigDecimal totalPremium = policyRepository.sumPremiumActive();
        BigDecimal avgPremium = policyRepository.avgPremiumActive();
        if (totalPremium == null) totalPremium = BigDecimal.ZERO;
        if (avgPremium == null) avgPremium = BigDecimal.ZERO;

        return PolicyKpis.builder()
                .activePolicies(active)
                .totalPremium(totalPremium)
                .averagePremium(avgPremium)
                .asOf(LocalDateTime.now())
                .build();
    }

    @Override
    public RenewalRatesSummary getRenewalRates(LocalDate from, LocalDate to, String periodType) {
        List<LocalDate> periodStarts = computePeriodStarts(from, to, periodType);
        List<RenewalRatePeriod> periods = new ArrayList<>();
        long totalRenewals = 0;
        long totalExpiring = 0;

        for (int i = 0; i < periodStarts.size(); i++) {
            LocalDate periodFrom = periodStarts.get(i);
            LocalDate periodTo = i + 1 < periodStarts.size() ? periodStarts.get(i + 1) : to.plusDays(1);
            String key = formatPeriodKey(periodFrom, periodType);

            LocalDateTime dtFrom = periodFrom.atStartOfDay();
            LocalDateTime dtTo = periodTo.atStartOfDay();

            long renewals = policyRepository.countRenewalsInPeriod(dtFrom, dtTo);
            long expiring = policyRepository.countExpiringInPeriod(periodFrom, periodTo);

            Double rate = expiring > 0 ? (double) renewals / expiring : null;
            periods.add(RenewalRatePeriod.builder()
                    .periodKey(key)
                    .renewalsCount(renewals)
                    .expiringCount(expiring)
                    .renewalRate(rate)
                    .build());
            totalRenewals += renewals;
            totalExpiring += expiring;
        }

        Double overall = totalExpiring > 0 ? (double) totalRenewals / totalExpiring : null;
        return RenewalRatesSummary.builder()
                .periods(periods)
                .overallRenewalRate(overall)
                .asOf(LocalDateTime.now())
                .build();
    }

    @Override
    public CancellationRatesSummary getCancellationRates(LocalDate from, LocalDate to, String periodType) {
        List<LocalDate> periodStarts = computePeriodStarts(from, to, periodType);
        List<CancellationRatePeriod> periods = new ArrayList<>();
        long total = 0;

        for (int i = 0; i < periodStarts.size(); i++) {
            LocalDate periodFrom = periodStarts.get(i);
            LocalDate periodTo = i + 1 < periodStarts.size() ? periodStarts.get(i + 1) : to.plusDays(1);
            String key = formatPeriodKey(periodFrom, periodType);

            LocalDateTime dtFrom = periodFrom.atStartOfDay();
            LocalDateTime dtTo = periodTo.atStartOfDay();

            long count = statusHistoryRepository.countByToStatusAndCreatedAtBetween(
                    PolicyStatus.CANCELLED, dtFrom, dtTo);
            periods.add(CancellationRatePeriod.builder()
                    .periodKey(key)
                    .cancellationsCount(count)
                    .build());
            total += count;
        }

        return CancellationRatesSummary.builder()
                .periods(periods)
                .totalCancellations(total)
                .asOf(LocalDateTime.now())
                .build();
    }

    @Override
    public PremiumSummary getPremiumSummary(String dimension) {
        List<PremiumSummaryRow> rows = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        if ("PRODUCT".equalsIgnoreCase(dimension)) {
            for (Object[] r : policyRepository.aggregatePremiumByProduct()) {
                String val = (String) r[0];
                long count = (Long) r[1];
                BigDecimal prem = (BigDecimal) r[2];
                if (prem == null) prem = BigDecimal.ZERO;
                rows.add(PremiumSummaryRow.builder()
                        .dimensionValue(val != null ? val : "N/A")
                        .policyCount(count)
                        .totalPremium(prem)
                        .build());
                grandTotal = grandTotal.add(prem);
            }
        } else if ("BRANCH".equalsIgnoreCase(dimension) || "REGION".equalsIgnoreCase(dimension)) {
            for (Object[] r : policyRepository.aggregatePremiumByBranch()) {
                String val = (String) r[0];
                long count = (Long) r[1];
                BigDecimal prem = (BigDecimal) r[2];
                if (prem == null) prem = BigDecimal.ZERO;
                rows.add(PremiumSummaryRow.builder()
                        .dimensionValue(val != null ? val : "N/A")
                        .policyCount(count)
                        .totalPremium(prem)
                        .build());
                grandTotal = grandTotal.add(prem);
            }
        } else if ("AGENT".equalsIgnoreCase(dimension)) {
            for (Object[] r : policyRepository.aggregatePremiumByAgent()) {
                String val = (String) r[0];
                long count = (Long) r[1];
                BigDecimal prem = (BigDecimal) r[2];
                if (prem == null) prem = BigDecimal.ZERO;
                rows.add(PremiumSummaryRow.builder()
                        .dimensionValue(val != null ? val : "N/A")
                        .policyCount(count)
                        .totalPremium(prem)
                        .build());
                grandTotal = grandTotal.add(prem);
            }
        }

        return PremiumSummary.builder()
                .dimension(dimension != null ? dimension.toUpperCase() : "PRODUCT")
                .rows(rows)
                .grandTotal(grandTotal)
                .asOf(LocalDateTime.now())
                .build();
    }

    @Override
    public PolicyTrends getPolicyTrends(LocalDate from, LocalDate to, String periodType) {
        List<LocalDate> periodStarts = computePeriodStarts(from, to, periodType);
        List<PolicyTrendPeriod> periods = new ArrayList<>();

        for (int i = 0; i < periodStarts.size(); i++) {
            LocalDate periodFrom = periodStarts.get(i);
            LocalDate periodTo = i + 1 < periodStarts.size() ? periodStarts.get(i + 1) : to.plusDays(1);
            String key = formatPeriodKey(periodFrom, periodType);

            LocalDateTime dtFrom = periodFrom.atStartOfDay();
            LocalDateTime dtTo = periodTo.atStartOfDay();

            long newPolicies = policyRepository.countNewPoliciesInPeriod(dtFrom, dtTo);
            long renewals = policyRepository.countRenewalsInPeriod(dtFrom, dtTo);
            long cancellations = statusHistoryRepository.countByToStatusAndCreatedAtBetween(
                    PolicyStatus.CANCELLED, dtFrom, dtTo);

            periods.add(PolicyTrendPeriod.builder()
                    .periodKey(key)
                    .newPolicies(newPolicies)
                    .renewals(renewals)
                    .cancellations(cancellations)
                    .build());
        }

        return PolicyTrends.builder()
                .periods(periods)
                .asOf(LocalDateTime.now())
                .build();
    }

    private List<LocalDate> computePeriodStarts(LocalDate from, LocalDate to, String periodType) {
        List<LocalDate> starts = new ArrayList<>();
        if ("WEEK".equalsIgnoreCase(periodType)) {
            LocalDate d = from.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            while (!d.isAfter(to)) {
                starts.add(d);
                d = d.plusWeeks(1);
            }
        } else if ("MONTH".equalsIgnoreCase(periodType)) {
            YearMonth ym = YearMonth.from(from);
            YearMonth end = YearMonth.from(to);
            while (!ym.isAfter(end)) {
                starts.add(ym.atDay(1));
                ym = ym.plusMonths(1);
            }
        } else {
            long days = ChronoUnit.DAYS.between(from, to) + 1;
            for (long i = 0; i < Math.min(days, 366); i++) {
                starts.add(from.plusDays(i));
            }
        }
        return starts;
    }

    private String formatPeriodKey(LocalDate d, String periodType) {
        if ("WEEK".equalsIgnoreCase(periodType)) {
            return d.getYear() + "-W" + String.format("%02d", (d.getDayOfYear() - 1) / 7 + 1);
        }
        if ("MONTH".equalsIgnoreCase(periodType)) {
            return YearMonth.from(d).toString();
        }
        return d.toString();
    }
}
