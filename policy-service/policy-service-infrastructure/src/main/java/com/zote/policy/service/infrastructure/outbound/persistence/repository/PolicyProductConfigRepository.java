package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyProductConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyProductConfigRepository extends JpaRepository<PolicyProductConfigEntity, String> {

    Optional<PolicyProductConfigEntity> findByProductId(String productId);

    List<PolicyProductConfigEntity> findAllByPolicyType(PolicyType policyType);

    boolean existsByProductId(String productId);
}
