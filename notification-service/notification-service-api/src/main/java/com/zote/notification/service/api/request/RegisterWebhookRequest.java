package com.zote.notification.service.api.request;

import jakarta.validation.constraints.NotBlank;
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
public class RegisterWebhookRequest {
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Webhook URL is required")
    @Pattern(regexp = "^https://.*", message = "Webhook URL must use HTTPS")
    private String url;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private List<String> eventTypes; // null = all events
    
    private String secret; // For signature verification
    
    private Map<String, String> headers; // Custom headers (e.g., Authorization)
    
    private boolean active = true;
}

