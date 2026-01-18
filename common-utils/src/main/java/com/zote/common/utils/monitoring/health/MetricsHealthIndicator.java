package com.zote.common.utils.monitoring.health;


import com.nimbusds.jose.util.health.HealthStatus;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsHealthIndicator implements HealthIndicator {

    private final MeterRegistry meterRegistry;
    private final AtomicReference<HealthStatus> healthStatus = new AtomicReference<>(HealthStatus.UP);
    private final Map<String, Instant> lastEventTimes = new ConcurrentHashMap<>();
    private final Map<String, Long> errorCounts = new ConcurrentHashMap<>();

    // Configuration thresholds
    private static final long ERROR_THRESHOLD = 100; // Maximum errors per minute
    private static final long NO_EVENT_THRESHOLD_MINUTES = 5; // No events for 5 minutes = unhealthy
    private static final double HIGH_LATENCY_THRESHOLD_MS = 5000; // 5 seconds
    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        try {
            // Check various metric conditions
            boolean hasHighErrorRate = checkErrorRate();
            boolean hasNoRecentEvents = checkRecentEvents();
            boolean hasHighLatency = checkLatency();
            boolean isRegistryHealthy = checkMeterRegistry();

            // Determine overall health
            if (!isRegistryHealthy) {
                healthStatus.set(HealthStatus.DOWN);
                return Health.down()
                    .withDetail("reason", "Meter registry unhealthy")
                    .withDetail("timestamp", Instant.now())
                    .build();
            }

            if (hasHighErrorRate) {
                healthStatus.set(HealthStatus.DEGRADED);
                return Health.status("DEGRADED")
                    .withDetail("reason", "High error rate detected")
                    .withDetail("error_count", getTotalErrorCount())
                    .withDetail("threshold", ERROR_THRESHOLD)
                    .withDetail("timestamp", Instant.now())
                    .build();
            }

            if (hasNoRecentEvents) {
                healthStatus.set(HealthStatus.DEGRADED);
                return Health.status("DEGRADED")
                    .withDetail("reason", "No recent events detected")
                    .withDetail("threshold_minutes", NO_EVENT_THRESHOLD_MINUTES)
                    .withDetail("last_event", getOldestLastEventTime())
                    .withDetail("timestamp", Instant.now())
                    .build();
            }

            if (hasHighLatency) {
                healthStatus.set(HealthStatus.DEGRADED);
                return Health.status("DEGRADED")
                    .withDetail("reason", "High latency detected")
                    .withDetail("threshold_ms", HIGH_LATENCY_THRESHOLD_MS)
                    .withDetail("timestamp", Instant.now())
                    .build();
            }

            healthStatus.set(HealthStatus.UP);
            return Health.up()
                .withDetail("metrics_count", meterRegistry.getMeters().size())
                .withDetail("last_checked", Instant.now())
                .withDetail("registry_type", meterRegistry.getClass().getSimpleName())
                .build();

        } catch (Exception e) {
            log.error("Error checking metrics health", e);
            healthStatus.set(HealthStatus.DOWN);
            return Health.down(e)
                .withDetail("reason", "Exception during health check")
                .withDetail("exception", e.getMessage())
                .withDetail("timestamp", Instant.now())
                .build();
        }
    }

    /**
     * Record an event for health monitoring
     */
    public void recordEvent(String eventType) {
        lastEventTimes.put(eventType, Instant.now());

        // Record event count metric
        meterRegistry.counter("metrics.health.events",
            "event_type", eventType,
            "application", getApplicationName()
        ).increment();
    }

    /**
     * Record an error for health monitoring
     */
    public void recordError(String errorType, String component) {
        String key = errorType + ":" + component;
        errorCounts.merge(key, 1L, Long::sum);

        // Record error metric
        meterRegistry.counter("metrics.health.errors",
            "error_type", errorType,
            "component", component,
            "application", getApplicationName()
        ).increment();

        // Also record for time-based tracking
        meterRegistry.counter("metrics.health.errors.minute",
            "error_type", errorType
        ).increment();
    }

    /**
     * Record latency for health monitoring
     */
    public void recordLatency(String operation, long latencyMs) {
        meterRegistry.timer("metrics.health.latency",
            "operation", operation,
            "application", getApplicationName()
        ).record(Duration.ofMillis(latencyMs));

        // Check if latency exceeds threshold
        if (latencyMs > HIGH_LATENCY_THRESHOLD_MS) {
            log.warn("High latency detected for {}: {}ms", operation, latencyMs);
        }
    }

    /**
     * Get current health status
     */
    public HealthStatus getCurrentStatus() {
        return healthStatus.get();
    }

    /**
     * Reset error counts (useful for testing or after remediation)
     */
    public void resetErrorCounts() {
        errorCounts.clear();
        log.info("Error counts reset");
    }

    /**
     * Get health metrics summary
     */
    public Map<String, Object> getHealthMetrics() {
        return Map.of(
            "status", healthStatus.get().toString(),
            "total_metrics", meterRegistry.getMeters().size(),
            "total_errors", getTotalErrorCount(),
            "last_event_age_minutes", getMaxEventAgeMinutes(),
            "registry_healthy", checkMeterRegistry(),
            "error_rate_healthy", !checkErrorRate(),
            "events_healthy", !checkRecentEvents(),
            "latency_healthy", !checkLatency()
        );
    }

    // Private helper methods

    private boolean checkErrorRate() {
        long totalErrorsLastMinute = meterRegistry.find("metrics.health.errors.minute")
            .counters().stream()
            .mapToLong(counter -> (long) counter.count())
            .sum();

        return totalErrorsLastMinute > ERROR_THRESHOLD;
    }

    private boolean checkRecentEvents() {
        if (lastEventTimes.isEmpty()) {
            return false; // No events recorded yet
        }

        Instant oldestEvent = lastEventTimes.values().stream()
            .min(Instant::compareTo)
            .orElse(Instant.now());

        Duration age = Duration.between(oldestEvent, Instant.now());
        return age.toMinutes() > NO_EVENT_THRESHOLD_MINUTES;
    }

    private boolean checkLatency() {
        return meterRegistry.find("metrics.health.latency")
            .timers().stream()
            .anyMatch(timer -> timer.mean(TimeUnit.MINUTES) > HIGH_LATENCY_THRESHOLD_MS / 1000.0); // Convert to seconds
    }

    private boolean checkMeterRegistry() {
        try {
            // Check if registry is responsive
            int metricCount = meterRegistry.getMeters().size();
            log.debug("Meter registry has {} metrics", metricCount);
            return metricCount >= 0; // Basic sanity check
        } catch (Exception e) {
            log.error("Meter registry check failed", e);
            return false;
        }
    }

    private long getTotalErrorCount() {
        return errorCounts.values().stream()
            .mapToLong(Long::longValue)
            .sum();
    }

    private String getOldestLastEventTime() {
        return lastEventTimes.values().stream()
            .min(Instant::compareTo)
            .map(Instant::toString)
            .orElse("No events recorded");
    }

    private long getMaxEventAgeMinutes() {
        return lastEventTimes.values().stream()
            .mapToLong(eventTime ->
                Duration.between(eventTime, Instant.now()).toMinutes())
            .max()
            .orElse(0L);
    }

    private String getApplicationName() {
//        return meterRegistry.config().commonTags().stream()
//            .filter(tag -> "application".equals(tag.getKey()))
//            .map(Tag::getValue)
//            .findFirst()
//            .orElse("unknown");
        return "unknown";
    }

    // Health status enum
    public enum HealthStatus {
        UP, DOWN, DEGRADED
    }
}
