package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.PolicyAuditLog;
import com.zote.policy.service.domain.ports.outbound.PolicyAuditLogPort;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyAuditLogEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.PolicyAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Append-only audit log implementation (14.6). No update or delete operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PolicyAuditLogPortImpl implements PolicyAuditLogPort {

    private final PolicyAuditLogRepository repository;

    @Override
    @Transactional
    public void append(PolicyAuditLog log) {
        repository.save(PolicyAuditLogEntity.from(log));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyAuditLog> findByPolicyId(String policyId, Pageable pageable) {
        return repository.findByPolicyIdOrderByChangedAtDesc(policyId, pageable)
                .map(PolicyAuditLogEntity::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PolicyAuditLog> findByPolicyIdAndChangeType(String policyId, String changeType, Pageable pageable) {
        return repository.findByPolicyIdAndChangeTypeOrderByChangedAtDesc(policyId, changeType, pageable)
                .map(PolicyAuditLogEntity::toDto);
    }
}
