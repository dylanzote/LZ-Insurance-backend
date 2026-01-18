package com.zote.user.service.api.request;

import jakarta.validation.constraints.NotNull;

public record CreatePermissionRequest(
        @NotNull
        String name
) {
}
