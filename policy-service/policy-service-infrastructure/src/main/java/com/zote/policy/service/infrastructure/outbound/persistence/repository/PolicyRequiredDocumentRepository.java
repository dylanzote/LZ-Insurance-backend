package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.RequiredDocumentType;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyRequiredDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRequiredDocumentRepository extends JpaRepository<PolicyRequiredDocumentEntity, String> {

    List<PolicyRequiredDocumentEntity> findAllByProductConfigId(String productConfigId);

    List<PolicyRequiredDocumentEntity> findAllByProductConfigIdAndMandatoryTrue(String productConfigId);

    boolean existsByProductConfigIdAndDocumentType(String productConfigId, RequiredDocumentType documentType);
}
