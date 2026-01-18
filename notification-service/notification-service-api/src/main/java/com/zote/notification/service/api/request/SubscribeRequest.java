package com.zote.notification.service.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SubscribeRequest {
    @NotBlank
    private String userId;

    private List<String> topics;
}
