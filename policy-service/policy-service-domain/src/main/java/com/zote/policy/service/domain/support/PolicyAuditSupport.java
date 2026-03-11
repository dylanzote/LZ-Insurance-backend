package com.zote.policy.service.domain.support;

import com.zote.policy.service.domain.models.PolicyAuditLog;
import com.zote.policy.service.domain.ports.outbound.PolicyAuditLogPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Logs policy changes for audit trail (14.1).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PolicyAuditSupport {

    public static final String CHANGE_STATUS = "STATUS_CHANGE";
    public static final String CHANGE_CREATE = "POLICY_CREATED";
    public static final String CHANGE_ISSUE = "POLICY_ISSUED";
    public static final String CHANGE_ENDORSE = "POLICY_ENDORSED";
    public static final String CHANGE_CANCEL = "POLICY_CANCELLED";
    public static final String CHANGE_RENEW = "POLICY_RENEWED";
    public static final String CHANGE_SUSPEND = "POLICY_SUSPENDED";
    public static final String CHANGE_REINSTATE = "POLICY_REINSTATED";
    public static final String CHANGE_PAYMENT = "PAYMENT_RECORDED";
    public static final String CHANGE_DOCUMENT = "DOCUMENT_VERIFIED";

    private final PolicyAuditLogPort policyAuditLogPort;

    public void logChange(String policyId, String changeType, String description,
                          Map<String, Object> oldValue, Map<String, Object> newValue, String changedBy) {
        try {
            var logEntry = PolicyAuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .policyId(policyId)
                    .changeType(changeType)
                    .description(description)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .changedBy(changedBy != null ? changedBy : "SYSTEM")
                    .changedAt(LocalDateTime.now())
                    .build();
            policyAuditLogPort.append(logEntry);
        } catch (Exception e) {
            log.warn("Failed to append audit log for policy {}: {}", policyId, e.getMessage());
        }
    }

    public void logStatusChange(String policyId, String fromStatus, String toStatus, String reason, String changedBy) {
        logChange(policyId, CHANGE_STATUS, reason,
                fromStatus != null ? Map.of("status", fromStatus) : null,
                Map.of("status", toStatus),
                changedBy);
    }
}
