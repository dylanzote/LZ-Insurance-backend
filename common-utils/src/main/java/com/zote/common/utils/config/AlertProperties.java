package com.zote.common.utils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "notification.alerts")
@Data
public class AlertProperties {
    private double errorRateThreshold = 10.0;
    private double latencyThresholdMs = 5000.0;
    private long pendingQueueThreshold = 10000;
    private long dlqThreshold = 100;
    private List<String> adminEmails = List.of("admin@example.com");
}
