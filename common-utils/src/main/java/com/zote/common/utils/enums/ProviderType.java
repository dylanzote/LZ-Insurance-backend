package com.zote.common.utils.enums;

public enum ProviderType {
    SENDGRID, SES, SMTP,     // Email
    TWILIO, VONAGE, AFRICASTALKING,  // SMS
    FCM, APNS, ONESIGNAL,    // Push
    GENERIC_WEBHOOK, EXPO    // Other
}
