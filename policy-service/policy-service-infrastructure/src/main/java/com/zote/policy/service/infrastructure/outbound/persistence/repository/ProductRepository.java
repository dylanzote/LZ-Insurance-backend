package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.PolicyType;
import com.zote.policy.service.domain.models.Product;
import com.zote.policy.service.infrastructure.outbound.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    Optional<ProductEntity> findByCode(String code);

    boolean existsByCode(String code);

    Page<ProductEntity> findAllByPolicyTypeAndActiveTrue(PolicyType policyType, Pageable pageable);

    Page<ProductEntity> findAllByActiveTrue(Pageable pageable);
}
