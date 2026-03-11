package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.infrastructure.outbound.entities.CancellationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancellationRequestRepository extends JpaRepository<CancellationRequestEntity, String> {

    Page<CancellationRequestEntity> findByPolicyId(String policyId, Pageable pageable);

    List<CancellationRequestEntity> findByPolicyIdAndStatus(String policyId, CancellationRequestStatus status);
}
