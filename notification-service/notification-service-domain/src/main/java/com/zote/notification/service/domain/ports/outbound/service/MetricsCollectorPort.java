package com.zote.notification.service.domain.ports.outbound.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface MetricsCollectorPort {
    void incrementCounter(String name, String... tags);
    void recordTimer(String name, long duration, TimeUnit unit, String... tags);
    void gauge(String name, Number value, String... tags);
    void histogram(String name, double value, String... tags);
    Map<String, Object> getMetrics();
}
