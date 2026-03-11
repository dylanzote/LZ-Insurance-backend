package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.domain.models.EndorsementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EndorsementRequestRepositoryPort {

    EndorsementRequest save(EndorsementRequest request);

    Optional<EndorsementRequest> findById(String id);

    Page<EndorsementRequest> findByPolicyId(String policyId, Pageable pageable);

    List<EndorsementRequest> findByPolicyIdAndStatus(String policyId, EndorsementRequestStatus status);
}
