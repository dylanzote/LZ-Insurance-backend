package com.zote.user.service.infrastructure.outbound.persistence.repository;

import com.zote.user.service.infrastructure.outbound.entities.UserActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivityEntity, String> {

    List<UserActivityEntity> findByUser_IdOrderByCreatedAtDesc(String userId);

    @Query("SELECT a FROM UserActivityEntity a WHERE a.user.id = :userId ORDER BY a.createdAt DESC")
    List<UserActivityEntity> findActivitiesByUserId(String userId);

    @Query("SELECT a FROM UserActivityEntity a ORDER BY a.createdAt DESC")
    List<UserActivityEntity> findAllActivitiesOrderByCreatedAtDesc();
    
    long countByCreatedAtAfter(LocalDateTime since);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
