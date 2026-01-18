package com.zote.user.service.infrastructure.outbound.persistence.repository;

import com.zote.common.utils.enums.Status;
import com.zote.user.service.infrastructure.outbound.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByKeycloakUserId(String keycloakUserId);

    Optional<UserEntity> findByPhoneNumber(String phoneNumber);

    Optional<UserEntity> findByUserName(String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUserName(String userName);

    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    Page<UserEntity> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);
    
    @Query("SELECT DISTINCT u FROM UserEntity u " +
           "LEFT JOIN u.roles r " +
           "WHERE (:role IS NULL OR r.name = :role) " +
           "AND (:status IS NULL OR u.status = :statusEnum) " +
           "AND (:department IS NULL OR u.department = :department)")
    Page<UserEntity> findUsersFiltered(@Param("role") String role, 
                                       @Param("status") String status,
                                       @Param("statusEnum") Status statusEnum,
                                       @Param("department") String department, 
                                       Pageable pageable);
}
