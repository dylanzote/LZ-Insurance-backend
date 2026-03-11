package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.ProductAddonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAddonRepository extends JpaRepository<ProductAddonEntity, String> {

    List<ProductAddonEntity> findAllByProductId(String productId);

    boolean existsByProductIdAndCode(String productId, String code);
}
