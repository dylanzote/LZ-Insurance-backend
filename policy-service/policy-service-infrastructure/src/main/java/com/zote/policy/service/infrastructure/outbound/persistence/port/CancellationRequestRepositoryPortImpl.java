package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.policy.service.domain.enums.CancellationRequestStatus;
import com.zote.policy.service.domain.models.CancellationRequest;
import com.zote.policy.service.domain.ports.outbound.CancellationRequestRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.CancellationRequestEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.CancellationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class CancellationRequestRepositoryPortImpl implements CancellationRequestRepositoryPort {

    private final CancellationRequestRepository repository;

    @Override
    public CancellationRequest save(CancellationRequest request) {
        if (request.getId() == null || request.getId().isBlank()) {
            request.setId(UUID.randomUUID().toString());
        }
        var entity = CancellationRequestEntity.toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());
        return repository.save(entity).toDto();
    }

    @Override
    public Optional<CancellationRequest> findById(String id) {
        return repository.findById(id).map(CancellationRequestEntity::toDto);
    }

    @Override
    public Page<CancellationRequest> findByPolicyId(String policyId, Pageable pageable) {
        return repository.findByPolicyId(policyId, pageable).map(CancellationRequestEntity::toDto);
    }

    @Override
    public List<CancellationRequest> findByPolicyIdAndStatus(String policyId, CancellationRequestStatus status) {
        return repository.findByPolicyIdAndStatus(policyId, status).stream()
                .map(CancellationRequestEntity::toDto)
                .toList();
    }
}
