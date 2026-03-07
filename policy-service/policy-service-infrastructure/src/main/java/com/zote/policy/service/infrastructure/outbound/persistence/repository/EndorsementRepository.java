package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.infrastructure.outbound.entities.EndorsementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EndorsementRepository extends JpaRepository<EndorsementEntity, String> {

    Page<EndorsementEntity> findAllByPolicyId(String policyId, Pageable pageable);

    List<EndorsementEntity> findAllByPolicyIdOrderByEffectiveDateDesc(String policyId);

    Page<EndorsementEntity> findAllByPolicyIdAndType(String policyId, EndorsementType type, Pageable pageable);

    Page<EndorsementEntity> findAllByPolicyIdAndEffectiveDateBetween(String policyId, LocalDate from, LocalDate to, Pageable pageable);
}
