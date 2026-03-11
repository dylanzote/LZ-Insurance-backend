package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.PolicyAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Append-only audit log (14.1, 14.6). No update or delete.
 */
public interface PolicyAuditLogPort {

    void append(PolicyAuditLog log);

    Page<PolicyAuditLog> findByPolicyId(String policyId, Pageable pageable);

    Page<PolicyAuditLog> findByPolicyIdAndChangeType(String policyId, String changeType, Pageable pageable);
}
