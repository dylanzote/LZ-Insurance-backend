package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.ProductRatingFactorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRatingFactorRepository extends JpaRepository<ProductRatingFactorEntity, String> {

    List<ProductRatingFactorEntity> findAllByProductId(String productId);

    List<ProductRatingFactorEntity> findAllByProductIdAndRequiredTrue(String productId);

    boolean existsByProductIdAndCode(String productId, String code);
}
