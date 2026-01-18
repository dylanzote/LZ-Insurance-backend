package com.zote.user.service.domain.support;

import com.zote.user.service.domain.model.PasswordStrength;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service for password policy enforcement and validation
 * 
 * Features:
 * - Password strength validation
 * - Common password detection
 * - Complexity requirements checking
 * - Suggestions for improvement
 */
@Service
@Slf4j
public class PasswordPolicyService {
    
    // Password policy configuration
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 128;
    private static final boolean REQUIRE_UPPERCASE = true;
    private static final boolean REQUIRE_LOWERCASE = true;
    private static final boolean REQUIRE_DIGIT = true;
    private static final boolean REQUIRE_SPECIAL_CHAR = true;
    
    // Common passwords (top 100 most common)
    private static final Set<String> COMMON_PASSWORDS = new HashSet<>();
    
    static {
        COMMON_PASSWORDS.addAll(List.of(
            "password", "123456", "12345678", "qwerty", "abc123", "monkey", "1234567",
            "letmein", "trustno1", "dragon", "baseball", "111111", "iloveyou", "master",
            "sunshine", "ashley", "bailey", "passw0rd", "shadow", "123123", "654321",
            "superman", "qazwsx", "michael", "football", "welcome", "jesus", "ninja",
            "mustang", "password1", "123456789", "adobe123", "admin", "1234567890"
        ));
    }
    
    /**
     * Validate password strength and provide feedback
     */
    public PasswordStrength validatePassword(String password) {
        List<String> suggestions = new ArrayList<>();
        List<String> requirements = new ArrayList<>();
        int score = 0;
        
        // Check length
        if (password == null || password.length() < MIN_LENGTH) {
            requirements.add("Password must be at least " + MIN_LENGTH + " characters long");
            suggestions.add("Use a longer password (minimum " + MIN_LENGTH + " characters)");
        } else {
            score += 20;
            if (password.length() >= 16) score += 10; // Bonus for longer passwords
            if (password.length() >= 20) score += 10; // Extra bonus
        }
        
        if (password != null && password.length() > MAX_LENGTH) {
            requirements.add("Password must not exceed " + MAX_LENGTH + " characters");
        }
        
        // Check uppercase
        if (REQUIRE_UPPERCASE) {
            if (password == null || !Pattern.compile("[A-Z]").matcher(password).find()) {
                requirements.add("Password must contain at least one uppercase letter");
                suggestions.add("Add uppercase letters (A-Z)");
            } else {
                score += 15;
            }
        }
        
        // Check lowercase
        if (REQUIRE_LOWERCASE) {
            if (password == null || !Pattern.compile("[a-z]").matcher(password).find()) {
                requirements.add("Password must contain at least one lowercase letter");
                suggestions.add("Add lowercase letters (a-z)");
            } else {
                score += 15;
            }
        }
        
        // Check digit
        if (REQUIRE_DIGIT) {
            if (password == null || !Pattern.compile("[0-9]").matcher(password).find()) {
                requirements.add("Password must contain at least one digit");
                suggestions.add("Add numbers (0-9)");
            } else {
                score += 15;
            }
        }
        
        // Check special character
        if (REQUIRE_SPECIAL_CHAR) {
            if (password == null || !Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) {
                requirements.add("Password must contain at least one special character");
                suggestions.add("Add special characters (!@#$%^&*...)");
            } else {
                score += 15;
            }
        }
        
        // Check for common passwords
        if (password != null && COMMON_PASSWORDS.contains(password.toLowerCase())) {
            requirements.add("This password is too common and easily guessable");
            suggestions.add("Avoid common passwords");
            score = Math.min(score, 20); // Cap score if using common password
        }
        
        // Check for repeated characters
        if (password != null && hasRepeatedCharacters(password)) {
            suggestions.add("Avoid repeated characters (e.g., 'aaa', '111')");
            score -= 10;
        }
        
        // Check for sequential characters
        if (password != null && hasSequentialCharacters(password)) {
            suggestions.add("Avoid sequential characters (e.g., 'abc', '123')");
            score -= 10;
        }
        
        // Bonus for character variety
        if (password != null) {
            int uniqueChars = (int) password.chars().distinct().count();
            if (uniqueChars > 10) score += 10;
        }
        
        // Cap score
        score = Math.max(0, Math.min(100, score));
        
        // Determine strength
        String strength;
        if (score < 40) {
            strength = "WEAK";
        } else if (score < 60) {
            strength = "MEDIUM";
        } else if (score < 80) {
            strength = "STRONG";
        } else {
            strength = "VERY_STRONG";
        }
        
        boolean meetsMinimum = requirements.isEmpty();
        
        return PasswordStrength.builder()
                .valid(meetsMinimum)
                .strength(strength)
                .score(score)
                .suggestions(suggestions)
                .requirements(requirements)
                .meetsMinimumRequirements(meetsMinimum)
                .build();
    }
    
    /**
     * Check if password has repeated characters (3 or more in a row)
     */
    private boolean hasRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            if (password.charAt(i) == password.charAt(i + 1) && 
                password.charAt(i) == password.charAt(i + 2)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if password has sequential characters (3 or more in a row)
     */
    private boolean hasSequentialCharacters(String password) {
        String lower = password.toLowerCase();
        for (int i = 0; i < lower.length() - 2; i++) {
            char c1 = lower.charAt(i);
            char c2 = lower.charAt(i + 1);
            char c3 = lower.charAt(i + 2);
            
            // Check for sequential numbers or letters
            if ((c2 == c1 + 1 && c3 == c2 + 1) || (c2 == c1 - 1 && c3 == c2 - 1)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get current password policy configuration
     */
    public PasswordPolicyConfig getPasswordPolicy() {
        return new PasswordPolicyConfig(
            MIN_LENGTH,
            MAX_LENGTH,
            REQUIRE_UPPERCASE,
            REQUIRE_LOWERCASE,
            REQUIRE_DIGIT,
            REQUIRE_SPECIAL_CHAR,
            5, // Password history count
            0, // Max age days (0 = no expiry)
            true // Prevent common passwords
        );
    }
    
    /**
     * Password policy configuration record
     */
    public record PasswordPolicyConfig(
        int minLength,
        int maxLength,
        boolean requireUppercase,
        boolean requireLowercase,
        boolean requireDigit,
        boolean requireSpecialChar,
        int passwordHistoryCount,
        int maxAgeDays,
        boolean preventCommonPasswords
    ) {}
}

