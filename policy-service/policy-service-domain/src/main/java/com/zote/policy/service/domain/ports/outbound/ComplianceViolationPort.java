package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.models.ComplianceViolation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Compliance violations storage (14.3).
 */
public interface ComplianceViolationPort {

    ComplianceViolation save(ComplianceViolation violation);

    Page<ComplianceViolation> findByPolicyId(String policyId, Pageable pageable);

    Page<ComplianceViolation> findByStatus(String status, Pageable pageable);

    List<ComplianceViolation> findOpenByPolicyId(String policyId);
}
