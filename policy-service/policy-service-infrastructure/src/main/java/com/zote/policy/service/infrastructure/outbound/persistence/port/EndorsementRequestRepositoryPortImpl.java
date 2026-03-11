package com.zote.policy.service.infrastructure.outbound.persistence.port;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.policy.service.domain.enums.EndorsementRequestStatus;
import com.zote.policy.service.domain.models.EndorsementRequest;
import com.zote.policy.service.domain.ports.outbound.EndorsementRequestRepositoryPort;
import com.zote.policy.service.infrastructure.outbound.entities.EndorsementRequestEntity;
import com.zote.policy.service.infrastructure.outbound.persistence.repository.EndorsementRequestRepository;
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
public class EndorsementRequestRepositoryPortImpl implements EndorsementRequestRepositoryPort {

    private final EndorsementRequestRepository repository;

    @Override
    public EndorsementRequest save(EndorsementRequest request) {
        if (request.getId() == null || request.getId().isBlank()) {
            request.setId(UUID.randomUUID().toString());
        }
        var entity = EndorsementRequestEntity.toEntity(request);
        entity.setUpdatedAt(LocalDateTime.now());
        return repository.save(entity).toDto();
    }

    @Override
    public Optional<EndorsementRequest> findById(String id) {
        return repository.findById(id).map(EndorsementRequestEntity::toDto);
    }

    @Override
    public Page<EndorsementRequest> findByPolicyId(String policyId, Pageable pageable) {
        return repository.findByPolicyId(policyId, pageable).map(EndorsementRequestEntity::toDto);
    }

    @Override
    public List<EndorsementRequest> findByPolicyIdAndStatus(String policyId, EndorsementRequestStatus status) {
        return repository.findByPolicyIdAndStatus(policyId, status).stream()
                .map(EndorsementRequestEntity::toDto)
                .toList();
    }
}
