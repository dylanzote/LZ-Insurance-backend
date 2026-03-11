package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.ProductCoverageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCoverageRepository extends JpaRepository<ProductCoverageEntity, String> {

    List<ProductCoverageEntity> findAllByProductId(String productId);

    Optional<ProductCoverageEntity> findByProductIdAndCode(String productId, String code);

    boolean existsByProductIdAndCode(String productId, String code);
}
