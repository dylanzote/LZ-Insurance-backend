package com.zote.policy.service.infrastructure.scheduler;

import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import com.zote.policy.service.domain.ports.outbound.PolicyDefaultsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Scheduled renewal quote generation (13.1).
 * Automatically generates renewal quotes for expiring policies daily.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalGenerationScheduler {

    private final PolicyPort policyPort;
    private final PolicyDefaultsPort policyDefaultsPort;
    private final AutomationAuditService auditService;

    /** Runs daily at 02:00. */
    @Scheduled(cron = "${policy.renewal.generation.cron:0 0 2 * * ?}")
    public void generateRenewalQuotes() {
        int daysAhead = policyDefaultsPort.getRenewalReminderDaysAhead();
        var metadata = Map.<String, Object>of("daysAhead", daysAhead);
        var logEntry = auditService.startJob(AutomationAuditService.JOB_RENEWAL_GENERATION, metadata);
        try {
            log.info("Starting scheduled renewal quote generation (13.1) - {} days ahead", daysAhead);
            int generated = policyPort.generateRenewalQuotesForExpiringPolicies(daysAhead);
            log.info("Scheduled renewal generation completed: {} quotes generated", generated);
            auditService.completeJob(logEntry.getId(), generated, generated, 0, null);
        } catch (Exception e) {
            log.error("Scheduled renewal generation failed", e);
            auditService.completeJob(logEntry.getId(), 0, 0, 0, e.getMessage());
        }
    }
}
