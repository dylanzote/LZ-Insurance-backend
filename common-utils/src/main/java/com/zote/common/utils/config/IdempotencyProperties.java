package com.zote.common.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notification.idempotency")
@Data
public class IdempotencyProperties {
    private int ttlHours = 24; // Keep idempotency keys for 24 hours
    private boolean enabled = true;
}
