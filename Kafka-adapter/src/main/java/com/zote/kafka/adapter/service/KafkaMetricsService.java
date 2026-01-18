package com.zote.kafka.adapter.service;

import com.zote.common.utils.monitoring.service.MetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class KafkaMetricsService {

    private final MeterRegistry meterRegistry;

    private final MetricsService metricsService;
//    private final ConcurrentHashMap<String, Counter> produceCounters = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Counter> consumeCounters = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, Timer> produceTimers = new ConcurrentHashMap<>();
//
//    public void recordProduceSuccess(String topic, long durationMs) {
//        getProduceCounter(topic, "success").increment();
//        getProduceTimer(topic).record(durationMs, TimeUnit.MILLISECONDS);
//
//        // Record topic-specific metric
//        Counter.builder("kafka.produce.messages.total")
//                .tags("topic", topic, "status", "success")
//                .description("Total number of messages produced to Kafka by topic and status")
//                .register(meterRegistry)
//                .increment();
//    }
//
//    public void recordProduceFailure(String topic, long durationMs) {
//        getProduceCounter(topic, "failure").increment();
//        getProduceTimer(topic).record(durationMs, TimeUnit.MILLISECONDS);
//
//        Counter.builder("kafka.produce.messages.total")
//            .tags("topic", topic, "status", "failure")
//            .register(meterRegistry)
//            .increment();
//    }
//
//    public void recordConsumeSuccess(String topic) {
//        getConsumeCounter(topic, "success").increment();
//
//        Counter.builder("kafka.consume.messages.total")
//            .tags("topic", topic, "status", "success")
//            .register(meterRegistry)
//            .increment();
//    }
//
//    public void recordConsumeFailure(String topic) {
//        getConsumeCounter(topic, "failure").increment();
//
//        Counter.builder("kafka.consume.messages.total")
//            .tags("topic", topic, "status", "failure")
//            .register(meterRegistry)
//            .increment();
//    }
//
//    public void recordBatchProduce(String topic, int total, int success, long durationMs) {
//        Timer.builder("kafka.batch.produce.duration")
//            .tags("topic", topic)
//            .register(meterRegistry)
//            .record(durationMs, TimeUnit.MILLISECONDS);
//
//        Counter.builder("kafka.batch.messages.total")
//            .tags("topic", topic)
//            .register(meterRegistry)
//            .increment(total);
//
//        Counter.builder("kafka.batch.messages.success")
//            .tags("topic", topic)
//            .register(meterRegistry)
//            .increment(success);
//    }
//
//    public void recordConsumerLag(String topic, String groupId, long lag) {
//        meterRegistry.gauge("kafka.consumer.lag",
//            io.micrometer.core.instrument.Tags.of(
//                "topic", topic,
//                "group", groupId
//            ),
//            lag
//        );
//    }
//
//    private Counter getProduceCounter(String topic, String status) {
//        String key = topic + ":" + status;
//        return produceCounters.computeIfAbsent(key, k ->
//            Counter.builder("kafka.produce.messages")
//                .tags("topic", topic, "status", status)
//                .description("Number of messages produced to Kafka")
//                .register(meterRegistry));
//    }
//
//    private Counter getConsumeCounter(String topic, String status) {
//        String key = topic + ":" + status;
//        return consumeCounters.computeIfAbsent(key, k ->
//            Counter.builder("kafka.consume.messages")
//                .tags("topic", topic, "status", status)
//                .description("Number of messages consumed from Kafka")
//                .register(meterRegistry));
//    }
//
//    private Timer getProduceTimer(String topic) {
//        return produceTimers.computeIfAbsent(topic, k ->
//            Timer.builder("kafka.produce.duration")
//                .tags("topic", topic)
//                .description("Time taken to produce messages to Kafka")
//                .publishPercentiles(0.5, 0.95, 0.99)
//                .register(meterRegistry));
//    }

    public void recordProduceSuccess(String topic, long durationMs) {
        metricsService.recordDuration(
            "kafka.produce.duration",
            durationMs,
            TimeUnit.MILLISECONDS,
            Map.of("topic", topic, "status", "success")
        );

        metricsService.incrementCounter(
            "kafka.produce.messages",
            "Messages produced to Kafka",
            Map.of("topic", topic, "status", "success")
        );
    }

    public void recordProduceFailure(String topic, long durationMs) {
        metricsService.recordDuration(
            "kafka.produce.duration",
            durationMs,
            TimeUnit.MILLISECONDS,
            Map.of("topic", topic, "status", "failure")
        );

        metricsService.incrementCounter(
            "kafka.produce.errors",
            "Kafka produce errors",
            Map.of("topic", topic, "error_type", "produce_failure")
        );
    }

    public void recordConsumerLag(String topic, String groupId, long lag) {
        metricsService.setGauge(
            "kafka.consumer.lag",
            "Consumer lag in messages",
            lag,
            Map.of("topic", topic, "group", groupId)
        );
    }

    public void recordConsumerSuccess(String topic, String groupId, long durationMs) {
        metricsService.recordDuration(
            "kafka.consume.duration",
            durationMs,
            TimeUnit.MILLISECONDS,
            Map.of("topic", topic, "group", groupId, "status", "success")
        );

        metricsService.incrementCounter(
            "kafka.consume.messages",
            "Messages consumed from Kafka",
            Map.of("topic", topic, "group", groupId)
        );
    }

    public void recordBatchProduce(String topic, int total, int success, long durationMs) {
        metricsService.recordDuration(
            "kafka.batch.produce.duration",
            durationMs,
            TimeUnit.MILLISECONDS,
            Map.of("topic", topic)
        );

        metricsService.incrementCounter(
            "kafka.batch.messages.total",
            "Total messages in batch",
            total,
            Map.of("topic", topic)
        );

        metricsService.incrementCounter(
            "kafka.batch.messages.success",
            "Successfully sent messages in batch",
            success,
            Map.of("topic", topic)
        );
    }
}
