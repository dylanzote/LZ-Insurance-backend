package com.zote.policy.service.infrastructure.scheduler;

import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled overdue detection (13.3).
 * Marks DUE billing schedules as OVERDUE when past due date.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BillingMonitoringScheduler {

    private final PolicyPort policyPort;
    private final AutomationAuditService auditService;

    /** Runs daily at 01:00. */
    @Scheduled(cron = "${policy.billing.monitoring.cron:0 0 1 * * ?}")
    public void runPaymentMonitoring() {
        var logEntry = auditService.startJob(AutomationAuditService.JOB_OVERDUE_DETECTION, null);
        try {
            log.info("Starting scheduled payment monitoring (13.3)");
            int marked = policyPort.runPaymentMonitoring();
            log.info("Scheduled payment monitoring completed: {} schedules marked overdue", marked);
            auditService.completeJob(logEntry.getId(), marked, marked, 0, null);
        } catch (Exception e) {
            log.error("Scheduled payment monitoring failed", e);
            auditService.completeJob(logEntry.getId(), 0, 0, 0, e.getMessage());
        }
    }
}
