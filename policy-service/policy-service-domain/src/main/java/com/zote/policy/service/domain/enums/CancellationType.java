package com.zote.policy.service.domain.enums;

/**
 * Reason/category for policy cancellation.
 * Used for reporting and pro-rata logic.
 */
public enum CancellationType {
    /** Customer withdrawal during free-look period */
    FREE_LOOK,
    /** Non-payment of premiums */
    NON_PAYMENT,
    /** Customer request */
    REQUEST,
    /** Fraud or misrepresentation */
    FRAUD,
    /** Other/administrative */
    OTHER
}
