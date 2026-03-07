package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.domain.models.Endorsement;
import com.zote.policy.service.domain.ports.outbound.EndorsementRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.EndorsementEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.EndorsementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class EndorsementRepositoryPortImpl implements EndorsementRepositoryPort {

    private final EndorsementRepository endorsementRepository;

    @Override
    public Endorsement saveEndorsement(Endorsement endorsement) {
        log.info("Saving endorsement {}", endorsement);
        return endorsementRepository.save(EndorsementEntity.toEntity(endorsement)).toDto();
    }

    @Override
    public Endorsement findById(String id) {
        log.info("Getting endorsement with id {}", id);
        return endorsementRepository.findById(id)
                .map(EndorsementEntity::toDto)
                .orElseThrow(() -> new FunctionalError("could not find endorsement with id " + id));
    }

    @Override
    public void deleteById(String id) {
        log.info("Deleting endorsement with id {}", id);
        endorsementRepository.deleteById(id);
    }

    @Override
    public Page<Endorsement> findAllByPolicyId(String policyId, Pageable pageable) {
        log.info("Getting endorsements by policyId {} (paged)", policyId);
        return endorsementRepository.findAllByPolicyId(policyId, pageable).map(EndorsementEntity::toDto);
    }

    @Override
    public List<Endorsement> findAllByPolicyIdOrderByEffectiveDateDesc(String policyId) {
        log.info("Getting endorsements by policyId {} ordered by effectiveDate desc", policyId);
        return endorsementRepository.findAllByPolicyIdOrderByEffectiveDateDesc(policyId)
                .stream()
                .map(EndorsementEntity::toDto)
                .toList();
    }

    @Override
    public Page<Endorsement> findAllByPolicyIdAndType(String policyId, EndorsementType type, Pageable pageable) {
        log.info("Getting endorsements by policyId {} and type {} (paged)", policyId, type);
        return endorsementRepository.findAllByPolicyIdAndType(policyId, type, pageable).map(EndorsementEntity::toDto);
    }

    @Override
    public Page<Endorsement> findAllByPolicyIdAndEffectiveDateBetween(String policyId, LocalDate from, LocalDate to, Pageable pageable) {
        log.info("Getting endorsements by policyId {} and effectiveDate between {} and {} (paged)", policyId, from, to);
        return endorsementRepository
                .findAllByPolicyIdAndEffectiveDateBetween(policyId, from, to, pageable)
                .map(EndorsementEntity::toDto);
    }
}
