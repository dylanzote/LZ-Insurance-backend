package com.zote.policy.service.infrastructure.outbound.persistence.repository;

import com.zote.policy.service.domain.enums.PolicyStatus;
import com.zote.policy.service.domain.models.PolicySearchCriteria;
import com.zote.policy.service.infrastructure.outbound.entities.PolicyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<PolicyEntity, String>, JpaSpecificationExecutor<PolicyEntity> {

    Optional<PolicyEntity> findByPolicyNumber(String policyNumber);

    boolean existsByPolicyNumber(String policyNumber);

    Page<PolicyEntity> findAllByCustomerId(String customerId, Pageable pageable);

    Page<PolicyEntity> findAllByAgentId(String agentId, Pageable pageable);

    Page<PolicyEntity> findAllByBranchId(String branchId, Pageable pageable);

    Page<PolicyEntity> findAllByStatus(PolicyStatus status, Pageable pageable);

    Page<PolicyEntity> findAllByCustomerIdAndStatus(String customerId, PolicyStatus status, Pageable pageable);

    @Query("""
        select p from PolicyEntity p
        where p.effectiveDate <= :date and p.expiryDate >= :date
    """)
    Page<PolicyEntity> findActiveOnDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("""
        select p from PolicyEntity p
        where p.expiryDate between :from and :to
    """)
    Page<PolicyEntity> findExpiringBetween(@Param("from") LocalDate from,
                                          @Param("to") LocalDate to,
                                          Pageable pageable);

    @Query("""
        select p from PolicyEntity p
        where p.customerId = :customerId
          and (lower(p.policyNumber) like lower(concat('%', :q, '%'))
               or lower(p.productId) like lower(concat('%', :q, '%')))
    """)
    Page<PolicyEntity> searchCustomerPolicies(@Param("customerId") String customerId,
                                              @Param("q") String query,
                                              Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update PolicyEntity p
           set p.status = :status
         where p.id = :policyId
    """)
    int updateStatus(@Param("policyId") String policyId, @Param("status") PolicyStatus status);

    // ---- Analytics (Epic 16) ----

    long countByStatus(PolicyStatus status);

    @Query("select coalesce(sum(p.premiumTotal), 0) from PolicyEntity p where p.status = :status")
    BigDecimal sumPremiumByStatus(@Param("status") PolicyStatus status);

    @Query("select coalesce(sum(p.premiumTotal), 0) from PolicyEntity p where p.status = com.zote.policy.service.domain.enums.PolicyStatus.ACTIVE")
    BigDecimal sumPremiumActive();

    @Query("select coalesce(avg(p.premiumTotal), 0) from PolicyEntity p where p.status = com.zote.policy.service.domain.enums.PolicyStatus.ACTIVE")
    BigDecimal avgPremiumActive();

    @Query("select p.status, count(p) from PolicyEntity p group by p.status")
    List<Object[]> countGroupByStatus();

    @Query("""
        select count(p) from PolicyEntity p
        where p.createdAt >= :from and p.createdAt < :to
        and p.parentPolicyId is null
    """)
    long countNewPoliciesInPeriod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        select count(p) from PolicyEntity p
        where p.createdAt >= :from and p.createdAt < :to
        and p.parentPolicyId is not null
    """)
    long countRenewalsInPeriod(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("""
        select count(p) from PolicyEntity p
        where p.expiryDate >= :from and p.expiryDate < :to
    """)
    long countExpiringInPeriod(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
        select p.productId, count(p), coalesce(sum(p.premiumTotal), 0) from PolicyEntity p
        where p.status = 'ACTIVE'
        group by p.productId
    """)
    List<Object[]> aggregatePremiumByProduct();

    @Query("""
        select p.branchId, count(p), coalesce(sum(p.premiumTotal), 0) from PolicyEntity p
        where p.status = 'ACTIVE' and p.branchId is not null
        group by p.branchId
    """)
    List<Object[]> aggregatePremiumByBranch();

    @Query("""
        select p.agentId, count(p), coalesce(sum(p.premiumTotal), 0) from PolicyEntity p
        where p.status = 'ACTIVE' and p.agentId is not null
        group by p.agentId
    """)
    List<Object[]> aggregatePremiumByAgent();
}
