package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.infrastructure.outbound.entities.PolicyVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyVersionRepository extends JpaRepository<PolicyVersionEntity, String> {

    List<PolicyVersionEntity> findAllByPolicyIdOrderByVersionNoDesc(String policyId);

    Optional<PolicyVersionEntity> findByPolicyIdAndVersionNo(String policyId, int versionNo);

    @Query("""
        select v from PolicyVersionEntity v
        where v.policy.id = :policyId
          and v.effectiveTo is null
    """)
    Optional<PolicyVersionEntity> findCurrentVersion(@Param("policyId") String policyId);

    @Query("""
        select max(v.versionNo) from PolicyVersionEntity v
        where v.policy.id = :policyId
    """)
    Integer findMaxVersionNo(@Param("policyId") String policyId);

    boolean existsByPolicyIdAndVersionNo(String policyId, int versionNo);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PolicyVersionEntity v
           set v.effectiveTo = :effectiveTo
         where v.id = :id
    """)
    int closeVersion(@Param("id") String versionId, @Param("effectiveTo") LocalDate effectiveTo);
}
