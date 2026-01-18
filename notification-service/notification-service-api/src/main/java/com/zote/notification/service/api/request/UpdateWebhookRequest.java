package com.zote.notification.service.api.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWebhookRequest {
    
    @Pattern(regexp = "^https://.*", message = "Webhook URL must use HTTPS")
    private String url;
    
    private String description;
    
    private List<String> eventTypes;
    
    private String secret;
    
    private Map<String, String> headers;
    
    private Boolean active;
}

