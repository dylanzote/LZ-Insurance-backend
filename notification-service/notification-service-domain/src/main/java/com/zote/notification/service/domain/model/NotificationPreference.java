package com.zote.notification.service.domain.model;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Notification preferences based on user role type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {
    private String userId;
    private boolean isCustomer; // true for CUSTOMER role, false for system roles
    
    // Enabled channels
    private Set<NotificationChannel> enabledChannels;
    
    // For customers
    private boolean sendPushNotifications;
    private boolean sendEmailNotifications;
    private boolean sendBilingualEmails; // true = send EN + FR, false = send only user language
    
    // For system roles
    private boolean sendWebNotifications;
    private boolean sendEmailForCritical; // Send email only for critical notifications
    
    // Common
    private Language preferredLanguage;
    private boolean quietHoursEnabled;
    private String quietHoursStart; // e.g., "22:00"
    private String quietHoursEnd; // e.g., "08:00"
    
    /**
     * Get default preferences for customer role
     */
    public static NotificationPreference defaultCustomerPreferences(String userId, Language language) {
        return NotificationPreference.builder()
                .userId(userId)
                .isCustomer(true)
                .enabledChannels(Set.of(
                        NotificationChannel.PUSH,
                        NotificationChannel.EMAIL
                ))
                .sendPushNotifications(true)
                .sendEmailNotifications(true)
                .sendBilingualEmails(true) // Customers get EN + FR emails
                .sendWebNotifications(false)
                .sendEmailForCritical(false)
                .preferredLanguage(language != null ? language : Language.EN)
                .quietHoursEnabled(false)
                .build();
    }
    
    /**
     * Get default preferences for system roles (admin, manager, etc.)
     */
    public static NotificationPreference defaultSystemRolePreferences(String userId, Language language) {
        return NotificationPreference.builder()
                .userId(userId)
                .isCustomer(false)
                .enabledChannels(Set.of(
                        NotificationChannel.WEB_SOCKET,
                        NotificationChannel.EMAIL
                ))
                .sendPushNotifications(false)
                .sendEmailNotifications(false) // Only critical
                .sendBilingualEmails(false) // System roles get single language
                .sendWebNotifications(true)
                .sendEmailForCritical(true) // Email only for critical notifications
                .preferredLanguage(language != null ? language : Language.EN)
                .quietHoursEnabled(false)
                .build();
    }
}

