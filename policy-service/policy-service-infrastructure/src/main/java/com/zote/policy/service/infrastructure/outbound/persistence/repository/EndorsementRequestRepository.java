package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.infrastructure.outbound.entities.EndorsementRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndorsementRequestRepository extends JpaRepository<EndorsementRequestEntity, String> {

    Page<EndorsementRequestEntity> findByPolicyId(String policyId, Pageable pageable);

    List<EndorsementRequestEntity> findByPolicyIdAndStatus(String policyId, EndorsementRequestStatus status);
}
