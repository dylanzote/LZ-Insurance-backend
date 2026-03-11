package com.zote.policy.service.infrastructure.scheduler;

import com.zote.policy.service.domain.ports.inbound.PolicyPort;
import com.zote.policy.service.domain.ports.outbound.PolicyDefaultsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Scheduled cancellation for non-payment (13.2).
 * Automatically cancels policies after grace period when billing is overdue.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NonPaymentCancellationScheduler {

    private final PolicyPort policyPort;
    private final PolicyDefaultsPort policyDefaultsPort;
    private final AutomationAuditService auditService;

    /** Runs daily at 03:00. */
    @Scheduled(cron = "${policy.non-payment.cancellation.cron:0 0 3 * * ?}")
    public void cancelPoliciesForNonPayment() {
        int gracePeriodDays = policyDefaultsPort.getDefaultGracePeriodDays();
        var metadata = Map.<String, Object>of("gracePeriodDays", gracePeriodDays);
        var logEntry = auditService.startJob(AutomationAuditService.JOB_NON_PAYMENT_CANCELLATION, metadata);
        try {
            log.info("Starting scheduled non-payment cancellation (13.2) - grace period {} days", gracePeriodDays);
            int cancelled = policyPort.cancelPoliciesForNonPayment(gracePeriodDays);
            log.info("Scheduled non-payment cancellation completed: {} policies cancelled", cancelled);
            auditService.completeJob(logEntry.getId(), cancelled, cancelled, 0, null);
        } catch (Exception e) {
            log.error("Scheduled non-payment cancellation failed", e);
            auditService.completeJob(logEntry.getId(), 0, 0, 0, e.getMessage());
        }
    }
}
