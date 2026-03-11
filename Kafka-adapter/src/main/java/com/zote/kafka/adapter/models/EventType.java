package com.zote.kafka.adapter.models;

import lombok.Data;
import lombok.Getter;

/**
 * Enum defining all event types in the system
 * Used for type-safe event handling across services
 */

@Getter
public enum EventType {
    // User Service Events
    USER_CREATED("USER_CREATED", "User account created"),
    USER_UPDATED("USER_UPDATED", "User profile updated"),
    USER_ACTIVATED("USER_ACTIVATED", "User account activated"),
    USER_SUSPENDED("USER_SUSPENDED", "User account suspended"),
    USER_DELETED("USER_DELETED", "User account deleted"),
    PASSWORD_CHANGED("PASSWORD_CHANGED", "User password changed"),
    PASSWORD_RESET_REQUESTED("PASSWORD_RESET_REQUESTED", "Password reset requested"),
    EMAIL_VERIFICATION_REQUESTED("EMAIL_VERIFICATION_REQUESTED", "Email verification requested"),
    EMAIL_VERIFIED("EMAIL_VERIFIED", "User email verified"),
    FAILED_LOGIN("FAILED_LOGIN", "Failed login attempt"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "Account locked due to failed login attempts"),
    TWO_FACTOR_CODE_SENT("TWO_FACTOR_CODE_SENT", "Two-factor authentication code sent"),
    
    // Notification Service Events
    NOTIFICATION_CREATED("NOTIFICATION_CREATED", "Notification created"),
    NOTIFICATION_SENT("NOTIFICATION_SENT", "Notification sent successfully"),
    NOTIFICATION_FAILED("NOTIFICATION_FAILED", "Notification failed to send"),
    NOTIFICATION_DELIVERED("NOTIFICATION_DELIVERED", "Notification delivered"),
    NOTIFICATION_READ("NOTIFICATION_READ", "Notification read by user"),

    POLICY_CREATED("POLICY_CREATED", "policy created"),
    POLICY_ISSUED("POLICY_ISSUED", "policy issued"),
    POLICY_ENDORSED("POLICY_ENDORSED", "policy endorsed"),
    POLICY_CANCELLED("POLICY_CANCELLED", "policy cancelled"),
    CANCELLATION_REQUESTED("CANCELLATION_REQUESTED", "cancellation requested by customer"),
    ENDORSEMENT_REQUESTED("ENDORSEMENT_REQUESTED", "endorsement requested"),
    ENDORSEMENT_REJECTED("ENDORSEMENT_REJECTED", "endorsement rejected"),

    QUOTE_CREATED("QUOTE_CREATED", "quote created"),
    QUOTE_ACCEPTED("QUOTE_ACCEPTED", "quote accepted"),
    QUOTE_REJECTED("QUOTE_REJECTED", "quote rejected"),
    UNDERWRITING_DECISION("UNDERWRITING_DECISION", "underwriting decision recorded"),
    RENEWAL_QUOTE_CREATED("RENEWAL_QUOTE_CREATED", "renewal quote created"),
    POLICY_RENEWED("POLICY_RENEWED", "policy renewed - new term started"),

    PAYMENT_RECORDED("PAYMENT_RECORDED", "payment recorded"),
    PAYMENT_FAILED("PAYMENT_FAILED", "payment failed"),
    PAYMENT_OVERDUE("PAYMENT_OVERDUE", "billing schedule marked overdue"),

    DOCUMENT_UPLOADED("DOCUMENT_UPLOADED", "document uploaded"),
    DOCUMENT_VERIFIED("DOCUMENT_VERIFIED", "document verified"),
    DOCUMENT_REJECTED("DOCUMENT_REJECTED", "document rejected"),
    DOCUMENT_DELETED("DOCUMENT_DELETED", "document deleted"),

    COMPLIANCE_VIOLATION_DETECTED("COMPLIANCE_VIOLATION_DETECTED", "compliance violation detected"),

    // Analytics events (16.8 - BI consumption)
    ANALYTICS_PORTFOLIO_SNAPSHOT("ANALYTICS_PORTFOLIO_SNAPSHOT", "portfolio snapshot published for BI"),

    // Property Service Events (if applicable)
    PROPERTY_CREATED("PROPERTY_CREATED", "Property created"),
    PROPERTY_UPDATED("PROPERTY_UPDATED", "Property updated"),
    
    // System Events
    SYSTEM_ERROR("SYSTEM_ERROR", "System error occurred"),
    HEALTH_CHECK("HEALTH_CHECK", "Service health check");

    private final String code;
    private final String description;

    EventType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Get EventType from string code
     */
    public static EventType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (EventType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Check if event type is a user service event
     */
    public boolean isUserServiceEvent() {
        return this == USER_CREATED || this == USER_UPDATED || 
               this == USER_ACTIVATED || this == USER_SUSPENDED || 
               this == USER_DELETED || this == PASSWORD_CHANGED || 
               this == PASSWORD_RESET_REQUESTED || this == EMAIL_VERIFICATION_REQUESTED ||
               this == EMAIL_VERIFIED || this == FAILED_LOGIN || this == ACCOUNT_LOCKED;
    }

    /**
     * Check if event type is a notification service event
     */
    public boolean isNotificationServiceEvent() {
        return this == NOTIFICATION_CREATED || this == NOTIFICATION_SENT || 
               this == NOTIFICATION_FAILED || this == NOTIFICATION_DELIVERED || 
               this == NOTIFICATION_READ;
    }
}

