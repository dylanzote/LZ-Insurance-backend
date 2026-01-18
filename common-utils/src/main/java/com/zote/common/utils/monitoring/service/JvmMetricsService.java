package com.zote.common.utils.monitoring.service;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "metrics.enable-jvm-metrics", havingValue = "true")
public class JvmMetricsService {

    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        log.info("Initializing JVM metrics collection");

        try {
            // JVM Memory metrics
            new JvmMemoryMetrics().bindTo(meterRegistry);

            // JVM GC metrics
            new JvmGcMetrics().bindTo(meterRegistry);

            // JVM Thread metrics
            new JvmThreadMetrics().bindTo(meterRegistry);

            // ClassLoader metrics
            new ClassLoaderMetrics().bindTo(meterRegistry);

            // System metrics
            new ProcessorMetrics().bindTo(meterRegistry);
            new UptimeMetrics().bindTo(meterRegistry);

            log.info("JVM metrics initialized successfully");

        } catch (Exception e) {
            log.error("Failed to initialize JVM metrics", e);
        }
    }

    /**
     * Monitor heap memory usage
     */
    public void monitorHeapMemory() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        meterRegistry.gauge("jvm.memory.heap.used", usedMemory);
        meterRegistry.gauge("jvm.memory.heap.free", freeMemory);
        meterRegistry.gauge("jvm.memory.heap.total", totalMemory);
        meterRegistry.gauge("jvm.memory.heap.max", maxMemory);
        meterRegistry.gauge("jvm.memory.heap.usage.percent",
            (double) usedMemory / maxMemory * 100);
    }

    /**
     * Monitor thread states
     */
    public void monitorThreadStates() {
        Thread.getAllStackTraces().keySet().stream()
            .collect(java.util.stream.Collectors.groupingBy(Thread::getState,
                java.util.stream.Collectors.counting()))
            .forEach((state, count) ->
                meterRegistry.gauge("jvm.threads.state",
                    java.util.List.of(
                        io.micrometer.core.instrument.Tag.of("state", state.name())
                    ), count)
            );
    }
}
