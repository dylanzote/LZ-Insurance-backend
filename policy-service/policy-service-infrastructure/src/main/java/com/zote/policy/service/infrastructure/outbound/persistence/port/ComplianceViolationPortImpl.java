package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.models.ComplianceViolation;
import com.zote.policy.service.domain.ports.outbound.ComplianceViolationPort;
import com.zote.policy.service.infrastructure.outbound.entities.ComplianceViolationEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.ComplianceViolationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceViolationPortImpl implements ComplianceViolationPort {

    private final ComplianceViolationRepository repository;

    @Override
    @Transactional
    public ComplianceViolation save(ComplianceViolation violation) {
        return repository.save(ComplianceViolationEntity.from(violation)).toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceViolation> findByPolicyId(String policyId, Pageable pageable) {
        return repository.findByPolicyIdOrderByDetectedAtDesc(policyId, pageable)
                .map(ComplianceViolationEntity::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ComplianceViolation> findByStatus(String status, Pageable pageable) {
        return repository.findByStatusOrderByDetectedAtDesc(status, pageable)
                .map(ComplianceViolationEntity::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplianceViolation> findOpenByPolicyId(String policyId) {
        return repository.findByPolicyIdAndStatus(policyId, "OPEN").stream()
                .map(ComplianceViolationEntity::toDto)
                .collect(Collectors.toList());
    }
}
