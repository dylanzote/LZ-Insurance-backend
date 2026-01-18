package com.zote.user.service.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RequestPasswordResetRequest(
        @NotNull
        @Email
        String email
) {
}

