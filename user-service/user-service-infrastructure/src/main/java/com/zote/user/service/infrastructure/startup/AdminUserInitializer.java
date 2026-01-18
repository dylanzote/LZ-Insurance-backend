package com.zote.user.service.infrastructure.startup;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.CreateUserData;
import com.zote.user.service.domain.model.Role;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.inbound.RolePort;
import com.zote.user.service.domain.ports.inbound.UserPort;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import com.zote.user.service.infrastructure.config.DefaultAdminProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Application startup initializer that creates a default admin user if none exists.
 * This ensures the system always has at least one admin user for initial setup.
 * 
 * <p>Features:
 * <ul>
 *   <li>Idempotent - safe to run multiple times</li>
 *   <li>Checks for existing admin users before creation</li>
 *   <li>Creates user in both database and Keycloak</li>
 *   <li>Configurable via application properties</li>
 *   <li>Can be disabled via configuration</li>
 * </ul>
 * 
 * <p>Configuration:
 * <pre>
 * app.default-admin.enabled=true
 * app.default-admin.email=admin@company.com
 * app.default-admin.password=ChangeMe123!
 * </pre>
 * 
 * @author LZ Insurance System
 * @since 1.0.0
 * @see DefaultAdminProperties
 */
@Slf4j
@Component
@Order(100) // Run after all other initializers
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {
    
    private final DefaultAdminProperties adminProperties;
    private final UserPort userPort;
    private final RolePort rolePort;
    private final UserRepositoryPort userRepository;
    
    /**
     * Executes on application startup to ensure a default admin user exists
     * 
     * @param args Application arguments (not used)
     * @throws Exception if admin user creation fails critically
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========================================");
        log.info("Starting Default Admin User Initialization...");
        log.info("========================================");
        
        if (!adminProperties.isEnabled()) {
            log.info("Default admin user creation is DISABLED in configuration");
            log.info("To enable, set: app.default-admin.enabled=true");
            return;
        }
        
        try {
            // Check if admin role exists
            Role adminRole = findAdminRole();
            if (adminRole == null) {
                log.error("CRITICAL: ADMIN role not found in database!");
                log.error("Please ensure default-data-changelog.xml has been executed properly");
                log.error("The system may not function correctly without the ADMIN role");
                return;
            }
            
            log.debug("ADMIN role found with ID: {}", adminRole.getId());
            
            // Check if any admin user already exists
            if (adminUserExists()) {
                log.info("✓ Admin user already exists - skipping creation");
                log.info("Current admin configuration:");
                logAdminConfiguration();
                return;
            }
            
            log.warn("⚠ No admin user found in the system!");
            log.info("Creating default admin user...");
            
            // Create the default admin user
            User createdAdmin = createDefaultAdminUser(adminRole);
            
            log.info("========================================");
            log.info("✓ DEFAULT ADMIN USER CREATED SUCCESSFULLY!");
            log.info("========================================");
            log.info("Email:    {}", createdAdmin.getEmail());
            log.info("Username: {}", createdAdmin.getUserName());
            log.info("Password: {} (configured in properties)", adminProperties.getPassword());
            log.info("========================================");
            log.warn("⚠ SECURITY WARNING:");
            log.warn("Please change the default admin password immediately after first login!");
            log.warn("Default credentials should NEVER be used in production environments!");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("❌ FAILED TO CREATE DEFAULT ADMIN USER!");
            log.error("========================================");
            log.error("Error: {}", e.getMessage(), e);
            log.error("The system may not be accessible without an admin user");
            log.error("Please check the logs and fix the issue");
            log.error("========================================");
            
            // Don't throw exception to allow application to start
            // This gives operators a chance to fix the issue manually
        }
    }
    
    /**
     * Finds the ADMIN role from the database
     * 
     * @return The admin role, or null if not found
     */
    private Role findAdminRole() {
        try {
            return rolePort.findRoleByName(adminProperties.getRoleName());
        } catch (Exception e) {
            log.error("Failed to find ADMIN role: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Checks if any user with ADMIN role already exists in the system
     * 
     * @return true if at least one admin user exists, false otherwise
     */
    private boolean adminUserExists() {
        try {
            // Get all users (in a real-world scenario, you might want to optimize this
            // by adding a specific repository method to check for admin users)
            List<User> allUsers = userPort.getAllUsers();
            
            if (allUsers == null || allUsers.isEmpty()) {
                log.debug("No users found in the system");
                return false;
            }
            
            // Check if any user has the ADMIN role
            boolean hasAdmin = allUsers.stream()
                    .filter(user -> user.getRoles() != null)
                    .flatMap(user -> user.getRoles().stream())
                    .anyMatch(role -> adminProperties.getRoleName().equalsIgnoreCase(role.getName()));
            
            if (hasAdmin) {
                long adminCount = allUsers.stream()
                        .filter(user -> user.getRoles() != null)
                        .filter(user -> user.getRoles().stream()
                                .anyMatch(role -> adminProperties.getRoleName().equalsIgnoreCase(role.getName())))
                        .count();
                log.debug("Found {} admin user(s) in the system", adminCount);
            }
            
            return hasAdmin;
            
        } catch (Exception e) {
            log.error("Error checking for existing admin users: {}", e.getMessage());
            // If we can't determine, assume admin exists to prevent duplicate creation
            return true;
        }
    }
    
    /**
     * Creates the default admin user using the configured properties
     * 
     * @param adminRole The admin role to assign to the user
     * @return The created admin user
     * @throws RuntimeException if user creation fails
     */
    private User createDefaultAdminUser(Role adminRole) {
        log.debug("Building admin user data from configuration...");
        
        // Build the admin user data using CreateUserData (not CreateAdminUserData)
        // This allows us to set a specific password without requiring an authenticated admin
        CreateUserData adminUserData = CreateUserData.builder()
                .firstName(adminProperties.getFirstName())
                .lastName(adminProperties.getLastName())
                .userName(adminProperties.getUserName())
                .email(adminProperties.getEmail())
                .phoneNumber(adminProperties.getPhoneNumber())
                .password(adminProperties.getPassword())
                .gender(adminProperties.getGender())
                .language(adminProperties.getLanguage())
                .dateOfBirth(adminProperties.getDateOfBirth())
                .town(adminProperties.getTown())
                .address(adminProperties.getAddress())
                .department(adminProperties.getDepartment())
                .roleIds(Set.of(adminRole.getId()))
                .build();
        
        log.debug("Creating admin user in database and Keycloak...");
        log.debug("This will create the user with the configured password");
        
        // Create the user using the regular createUser method
        // This method accepts a password directly and doesn't require an authenticated admin
        User createdUser = userPort.createUser(adminUserData);
        
        if (createdUser == null) {
            throw new FunctionalError("User creation returned null - operation failed");
        }
        
        log.debug("Admin user created with ID: {}", createdUser.getId());
        log.debug("Admin user can now login with the configured credentials");
        
        return createdUser;
    }
    
    /**
     * Logs the current admin configuration for debugging purposes
     */
    private void logAdminConfiguration() {
        log.debug("Configured admin username: {}", adminProperties.getUserName());
        log.debug("Configured admin email: {}", adminProperties.getEmail());
        log.debug("Configured admin role: {}", adminProperties.getRoleName());
        log.debug("Auto-creation enabled: {}", adminProperties.isEnabled());
    }
}
