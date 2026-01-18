package com.zote.user.service.infrastructure.config;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the default admin user
 * These credentials are used to create an initial admin user on first system startup
 * 
 * @author LZ Insurance System
 * @since 1.0.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.default-admin")
public class DefaultAdminProperties {
    
    /**
     * Whether to enable automatic default admin creation
     * Default: true
     */
    private boolean enabled = true;
    
    /**
     * Admin first name
     * Default: "System"
     */
    private String firstName = "Admin";
    
    /**
     * Admin last name
     * Default: "Administrator"
     */
    private String lastName = "Admin";
    
    /**
     * Admin username
     * Default: "admin"
     */
    private String userName = "Admin";
    
    /**
     * Admin email address
     * This is required and should be changed in production
     * Default: "admin@lz-insurance.com"
     */
    private String email = "admin@lz-insurance.com";
    
    /**
     * Admin phone number
     * Default: "+237000000000"
     */
    private String phoneNumber = "671539720";
    
    /**
     * Admin default password
     * IMPORTANT: This should be changed immediately after first login in production
     * Default: "Admin@2024!"
     */
    private String password = "Admin@2026!";
    
    /**
     * Admin gender
     * Default: OTHER
     */
    private Gender gender = Gender.MALE;
    
    /**
     * Admin language preference
     * Default: EN (English)
     */
    private Language language = Language.EN;
    
    /**
     * Admin date of birth
     * Format: dd/MM/yyyy
     * Default: "01/01/1990"
     */
    private String dateOfBirth = "01/01/1990";
    
    /**
     * Admin town/city
     * Default: "Headquarters"
     */
    private String town = "Headquarters";
    
    /**
     * Admin address
     * Default: "Main Office"
     */
    private String address = "Main Office";
    
    /**
     * Admin department
     * Default: "Administration"
     */
    private String department = "Administration";
    
    /**
     * Admin role name to assign
     * Default: "ADMIN"
     */
    private String roleName = "ADMIN";
}
