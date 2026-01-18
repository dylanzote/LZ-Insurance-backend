package com.zote.user.service.api.request;

import jakarta.validation.constraints.NotNull;

public record UpdatePermissionRequest(
        @NotNull
        String name,
        @NotNull
        String id
) {
}
