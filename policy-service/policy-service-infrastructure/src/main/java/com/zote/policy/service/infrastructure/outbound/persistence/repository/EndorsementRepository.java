package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.EndorsementType;
import com.zote.policy.service.infrastructure.outbound.entities.EndorsementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EndorsementRepository extends JpaRepository<EndorsementEntity, String> {

    @Query("SELECT e FROM EndorsementEntity e WHERE e.policy.id = :policyId")
    Page<EndorsementEntity> findAllByPolicyId(@Param("policyId") String policyId, Pageable pageable);

    @Query("SELECT e FROM EndorsementEntity e WHERE e.policy.id = :policyId ORDER BY e.effectiveDate DESC")
    List<EndorsementEntity> findAllByPolicyIdOrderByEffectiveDateDesc(@Param("policyId") String policyId);

    @Query("SELECT e FROM EndorsementEntity e WHERE e.policy.id = :policyId AND e.type = :type")
    Page<EndorsementEntity> findAllByPolicyIdAndType(@Param("policyId") String policyId, @Param("type") EndorsementType type, Pageable pageable);

    @Query("SELECT e FROM EndorsementEntity e WHERE e.policy.id = :policyId AND e.effectiveDate BETWEEN :from AND :to")
    Page<EndorsementEntity> findAllByPolicyIdAndEffectiveDateBetween(@Param("policyId") String policyId, @Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);
}
