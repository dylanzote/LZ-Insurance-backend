package com.zote.notification.service.domain.service;

import com.zote.common.utils.enums.Language;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.domain.model.NotificationPreference;
import com.zote.notification.service.domain.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service to determine notification preferences based on user role
 * 
 * Rules:
 * - Customers: Push (mobile) or Web notifications + Bilingual Email (EN + FR)
 * - System roles: Web notifications + Email (critical only)
 */
@Service
@Slf4j
public class NotificationPreferenceService {

    /**
     * Get notification preference for user based on their role
     */
    public NotificationPreference getPreferenceForUser(UserInfo userInfo) {
        if (userInfo == null) {
            log.warn("UserInfo is null, using default system role preferences");
            return NotificationPreference.defaultSystemRolePreferences("unknown", Language.EN);
        }

        if (userInfo.getRoles() == null || userInfo.getRoles().isEmpty()) {
            log.warn("User {} has no roles, using default system role preferences", userInfo.getId());
            return NotificationPreference.defaultSystemRolePreferences(userInfo.getId(), 
                    getLanguageFromLocale(userInfo.getLocale()));
        }

        // Check if user has ANY customer role
        boolean isCustomer = userInfo.getRoles().stream()
                .anyMatch(UserInfo.UserRole::isCustomerRole);
        
        Language language = getLanguageFromLocale(userInfo.getLocale());

        if (isCustomer) {
            log.debug("User {} has CUSTOMER role(s), applying customer notification preferences", 
                    userInfo.getId());
            return NotificationPreference.defaultCustomerPreferences(userInfo.getId(), language);
        } else {
            log.debug("User {} has system role(s) only, applying system notification preferences", 
                    userInfo.getId());
            return NotificationPreference.defaultSystemRolePreferences(userInfo.getId(), language);
        }
    }

    /**
     * Determine channels to use for notification based on preferences
     */
    public Set<NotificationChannel> getChannelsForNotification(
            NotificationPreference preference, 
            boolean isCritical) {
        
        Set<NotificationChannel> channels = new HashSet<>();

        if (preference.isCustomer()) {
            // Customers: Push/Web + Email
            if (preference.isSendPushNotifications()) {
                channels.add(NotificationChannel.PUSH);
            }
            if (preference.isSendEmailNotifications()) {
                channels.add(NotificationChannel.EMAIL);
            }
        } else {
            // System roles: Web + Email (critical only)
            if (preference.isSendWebNotifications()) {
                channels.add(NotificationChannel.WEB_SOCKET);
            }
            if (isCritical && preference.isSendEmailForCritical()) {
                channels.add(NotificationChannel.EMAIL);
            }
        }

        return channels;
    }

    /**
     * Check if user should receive bilingual emails
     */
    public boolean shouldSendBilingualEmail(NotificationPreference preference) {
        return preference.isCustomer() && preference.isSendBilingualEmails();
    }


    /**
     * Convert locale string to Language enum
     */
    private Language getLanguageFromLocale(String locale) {
        if (locale == null || locale.isEmpty()) {
            return Language.EN;
        }

        // Extract language code from locale (e.g., "en" from "en_US")
        String langCode = locale.toLowerCase().split("[_-]")[0];

        return switch (langCode) {
            case "fr" -> Language.FR;
            case "en" -> Language.EN;
            default -> Language.EN;
        };
    }
}

