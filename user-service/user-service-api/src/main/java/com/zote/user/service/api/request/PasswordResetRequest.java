package com.zote.user.service.api.request;

import jakarta.validation.constraints.NotNull;

public record PasswordResetRequest(
        @NotNull
        String token,
        @NotNull
        String newPassword,
        String twoFactorCode  // Optional: 2FA code for sensitive operations
) {
}

