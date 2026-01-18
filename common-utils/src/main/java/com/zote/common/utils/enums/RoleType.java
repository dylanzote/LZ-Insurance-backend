package com.zote.common.utils.enums;

import lombok.Getter;

/**
 * Role types in the insurance system
 * Used to categorize users and determine notification preferences
 */
@Getter
public enum RoleType {
    // Customer roles - customers using mobile/web apps
    CUSTOMER("CUSTOMER", "Insurance customer", true),
    
    // System/Internal roles - company staff
    ADMIN("ADMIN", "System administrator", false),
    MANAGER("MANAGER", "Insurance manager", false),
    AGENT("AGENT", "Insurance agent", false),
    SUPERVISOR("SUPERVISOR", "Department supervisor", false),
    UNDERWRITER("UNDERWRITER", "Insurance underwriter", false),
    CLAIMS_ADJUSTER("CLAIMS_ADJUSTER", "Claims adjuster", false),
    
    // Support roles
    SUPPORT("SUPPORT", "Customer support", false),
    ACCOUNTANT("ACCOUNTANT", "Accountant", false);

    private final String code;
    private final String description;
    private final boolean isCustomer;

    RoleType(String code, String description, boolean isCustomer) {
        this.code = code;
        this.description = description;
        this.isCustomer = isCustomer;
    }

    /**
     * Check if this role type is a customer role
     */
    public boolean isCustomerRole() {
        return isCustomer;
    }

    /**
     * Check if this role type is a system role (non-customer)
     */
    public boolean isSystemRole() {
        return !isCustomer;
    }

    /**
     * Get RoleType from string code
     */
    public static RoleType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (RoleType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}

