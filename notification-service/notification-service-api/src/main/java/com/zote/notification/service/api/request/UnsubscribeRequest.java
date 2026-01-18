package com.zote.notification.service.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class UnsubscribeRequest {
    @NotBlank
    private String userId;

    private List<String> topics;
}
