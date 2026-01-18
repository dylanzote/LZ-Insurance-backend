package com.zote.common.utils.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "metrics", ignoreInvalidFields = true)
public class MetricsProperties {
    @NotEmpty
    private String applicationName = "unknown";

    @NotEmpty
    private String region = "default";

    @NotEmpty
    private String zone = "default";

    @NotEmpty
    private String version = "1.0.0";

    @Min(5)
    private int stepSeconds = 30;

    @NotNull
    private List<Double> percentiles = List.of(0.5, 0.75, 0.95, 0.99, 0.999);

    @Min(0)
    private int percentilePrecision = 2;

    @Min(1)
    private long minimumExpectedValue = 1L;

    @Min(1)
    private long maximumExpectedValue = 60_000L;

    @Min(1)
    private int bufferLength = 3;

    private boolean enableJvmMetrics = true;
    private boolean enableSystemMetrics = true;
    private boolean enableLogMetrics = true;
    private boolean enableHttpMetrics = true;
    private boolean enableDatabaseMetrics = false;
    private boolean enableKafkaMetrics = true;
    private boolean enableCacheMetrics = false;

    @NestedConfigurationProperty
    private HttpMetrics http = new HttpMetrics();

    @NestedConfigurationProperty
    private DatabaseMetrics database = new DatabaseMetrics();

    @NestedConfigurationProperty
    private KafkaMetrics kafka = new KafkaMetrics();

    @NestedConfigurationProperty
    private CustomMetrics custom = new CustomMetrics();

    @NestedConfigurationProperty
    private HealthMetrics health = new HealthMetrics();

    @Data
    public static class HttpMetrics {
        private boolean enableClientMetrics = true;
        private boolean enableServerMetrics = true;
        private Duration clientTimeoutWarning = Duration.ofSeconds(5);
        private Duration serverTimeoutWarning = Duration.ofSeconds(10);
        private List<String> excludeUris = List.of("/actuator", "/health", "/metrics");
    }

    @Data
    public static class DatabaseMetrics {
        private boolean enableConnectionPoolMetrics = true;
        private boolean enableQueryMetrics = false;
        private Duration slowQueryThreshold = Duration.ofSeconds(2);
        private List<String> excludeSchemas = new ArrayList<>();
    }

    @Data
    public static class KafkaMetrics {
        private boolean enableProducerMetrics = true;
        private boolean enableConsumerMetrics = true;
        private boolean enableAdminMetrics = false;
        private Duration consumerLagWarning = Duration.ofMinutes(5);
        private Map<String, String> topicLabels = Map.of();
    }

    @Data
    public static class CustomMetrics {
        private Map<String, String> additionalTags = Map.of();
        private List<String> enabledBusinessMetrics = new ArrayList<>();
        private boolean enableCustomMeters = true;
    }

    @Data
    public static class HealthMetrics {
        private boolean enabled = true;
        private long errorThreshold = 100; // errors per minute
        private long noEventThresholdMinutes = 5;
        private double highLatencyThresholdMs = 5000;
        private List<String> criticalMetrics = List.of(
            "jvm.memory.used",
            "http.server.requests",
            "kafka.produce.messages",
            "kafka.consume.messages"
        );
        private Duration checkInterval = Duration.ofSeconds(30);
    }
}
