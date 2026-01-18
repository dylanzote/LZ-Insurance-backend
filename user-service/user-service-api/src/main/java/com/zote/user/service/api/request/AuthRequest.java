package com.zote.user.service.api.request;

import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @NotNull
        String email,
        @NotNull
        String password
) {
}
