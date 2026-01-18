package com.zote.user.service.domain.ports.outbound;

import com.zote.user.service.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryPort {

    User saveUser(User user);

    void deleteUserById(String userId);

    User findUserById(String userId);

    User findUserByKeyCloakId(String userId);

    User findUserByEmail(String email);

    User findUserByUserName(String username);

    User findUserByPhoneNumber(String phoneNumber);

    List<User> getAllUsers();

    Page<User> findUsersByPage(Pageable pageable);

    Page<User> findUsersAgentByPage(String roleName, Pageable pageable);
    
    Page<User> findUsersFiltered(String role, String status, String department, Pageable pageable);

    boolean existsById(String userId);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUserName(String username);
}
