package com.zote.policy.service.domain.ports.outbound;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.domain.models.CancellationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CancellationRequestRepositoryPort {

    CancellationRequest save(CancellationRequest request);

    Optional<CancellationRequest> findById(String id);

    Page<CancellationRequest> findByPolicyId(String policyId, Pageable pageable);

    List<CancellationRequest> findByPolicyIdAndStatus(String policyId, CancellationRequestStatus status);
}
