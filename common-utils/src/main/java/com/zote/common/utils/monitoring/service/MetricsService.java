package com.zote.common.utils.monitoring.service;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Counter> counterCache = new ConcurrentHashMap<>();
    private final Map<String, Timer> timerCache = new ConcurrentHashMap<>();
    private final Map<String, Gauge> gaugeCache = new ConcurrentHashMap<>();
    private final Map<String, DistributionSummary> summaryCache = new ConcurrentHashMap<>();

    /**
     * Increment a counter
     */
    public void incrementCounter(String name, String description, Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);
        Counter counter = counterCache.computeIfAbsent(cacheKey, key ->
            Counter.builder(name)
                .description(description)
                .tags(convertTags(tags))
                .register(meterRegistry)
        );
        counter.increment();
    }

    /**
     * Increment counter by specific amount
     */
    public void incrementCounter(String name, String description, double amount, Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);
        Counter counter = counterCache.computeIfAbsent(cacheKey, key ->
            Counter.builder(name)
                    .description(description)
                .tags(convertTags(tags))
                .register(meterRegistry)
        );
        counter.increment(amount);
    }

    /**
     * Record execution time
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Stop timer and record duration
     */
    public void stopTimer(Timer.Sample sample, String name, String description,
                         Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);
        Timer timer = timerCache.computeIfAbsent(cacheKey, key ->
            Timer.builder(name)
                .description(description)
                .tags(convertTags(tags))
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry)
        );
        sample.stop(timer);
    }

    /**
     * Record a pre-measured duration
     */
    public void recordDuration(String name, long duration, TimeUnit unit, Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);
        Timer timer = timerCache.computeIfAbsent(cacheKey, key ->
            Timer.builder(name)
                .tags(convertTags(tags))
                .register(meterRegistry)
        );
        timer.record(duration, unit);
    }

    /**
     * Create or update a gauge
     */
    public void setGauge(String name, String description, double value,
                        Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);

        // Gauges need to be registered with a supplier
        AtomicLong gaugeValue = new AtomicLong((long) value);

        Gauge gauge = Gauge.builder(name, gaugeValue::get)
            .description(description)
            .tags(convertTags(tags))
            .register(meterRegistry);

        gaugeCache.put(cacheKey, gauge);
    }

    /**
     * Create a gauge with a supplier function
     */
    public  void registerGauge(String name, String description, Supplier<Number> valueSupplier, Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);

        Gauge gauge = Gauge.builder(name, valueSupplier)
            .description(description)
            .tags(convertTags(tags))
            .register(meterRegistry);

        gaugeCache.put(cacheKey, gauge);
    }

    /**
     * Record a distribution summary (like histogram)
     */
    public void recordSummary(String name, double amount, Map<String, String> tags) {
        String cacheKey = buildCacheKey(name, tags);
        DistributionSummary summary = summaryCache.computeIfAbsent(cacheKey, key ->
            DistributionSummary.builder(name)
                .tags(convertTags(tags))
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry)
        );
        summary.record(amount);
    }

    /**
     * Record business event with tags
     */
    public void recordBusinessEvent(String eventType, String entityType,
                                   String entityId, Map<String, String> additionalTags) {
        Map<String, String> tags = Map.of(
            "event_type", eventType,
            "entity_type", entityType,
            "entity_id", entityId
        );

        if (additionalTags != null) {
            tags = Map.copyOf(additionalTags);
        }

        incrementCounter("business.event.total", "Business events", tags);
    }

    /**
     * Record error with context
     */
    public void recordError(String errorType, String service, String operation,
                           Map<String, String> context) {
        Map<String, String> tags = Map.of(
            "error_type", errorType,
            "service", service,
            "operation", operation
        );

        if (context != null) {
            tags = Map.copyOf(context);
        }

        incrementCounter("error.total", "Application errors", tags);
    }

    /**
     * Record API call metrics
     */
    public Timer.Sample recordApiCallStart(String endpoint, String method,
                                          String status) {
        Map<String, String> tags = Map.of(
            "endpoint", endpoint,
            "method", method,
            "status", status
        );

        return startTimer();
    }

    public void recordApiCallEnd(Timer.Sample sample, String endpoint,
                                String method, String status, boolean success) {
        Map<String, String> tags = Map.of(
            "endpoint", endpoint,
            "method", method,
            "status", status,
            "success", String.valueOf(success)
        );

        stopTimer(sample, "api.call.duration", "API call duration", tags);
        incrementCounter("api.call.total", "API calls", tags);

        if (!success) {
            incrementCounter("api.call.error", "API call errors", tags);
        }
    }

    private List<Tag> convertTags(Map<String, String> tags) {
        return tags.entrySet().stream()
            .map(entry -> Tag.of(entry.getKey(), entry.getValue()))
            .toList();
    }

    private String buildCacheKey(String name, Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return name;
        }

        StringBuilder key = new StringBuilder(name);
        tags.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> key.append(":").append(entry.getKey()).append("=").append(entry.getValue()));

        return key.toString();
    }

    /**
     * Get all registered metric names
     */
    public List<String> getRegisteredMetrics() {
        return meterRegistry.getMeters().stream()
            .map(meter -> meter.getId().getName())
            .distinct()
            .toList();
    }

    /**
     * Clear cached meters (useful for testing)
     */
    public void clearCache() {
        counterCache.clear();
        timerCache.clear();
        gaugeCache.clear();
        summaryCache.clear();
    }
}
