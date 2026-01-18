package com.zote.notification.service.infrastructure.adapters.providers.push;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification.providers.expo")
@Data
public class ExpoPushProviderConfig {
    private boolean enabled = false;
    private String accessToken; // Optional: for Expo's access token if using their paid service
}

