package com.zote.notification.service.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkReadRequest {
    @NotBlank
    private String notificationId;
}
