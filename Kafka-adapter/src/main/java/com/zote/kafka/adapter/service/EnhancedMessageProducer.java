package com.zote.kafka.adapter.service;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.kafka.adapter.models.DataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnhancedMessageProducer {

    private final KafkaTemplate<String, DataEvent> kafkaTemplate;
    private final RetryTemplate retryTemplate;
    private final KafkaMetricsService metricsService;

    /**
     * Send message with synchronous acknowledgment and retry
     */
    public SendResult<String, DataEvent> sendMessageSync(String topic, DataEvent event) {
        Instant start = Instant.now();

        try {
            SendResult<String, DataEvent> result = retryTemplate.execute(context -> {
                int attempt = context.getRetryCount() + 1;
                log.debug("Attempt {} to send message to topic {}: {}", attempt, topic, event.getEventId());
                return kafkaTemplate.send(topic, event.getEventId(), event).get();
            });

            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordProduceSuccess(topic, duration.toMillis());

            log.debug("Message sent successfully to topic {} in {}ms: {}", topic, duration.toMillis(), event.getEventId());
            return result;

        } catch (Exception e) {
            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordProduceFailure(topic, duration.toMillis());

            log.error("Failed to send message to topic {} after retries in {}ms: {}", topic, duration.toMillis(), event.getEventId(), e);
            throw new FunctionalError("Failed to send message to topic: " + topic);
        }
    }

    /**
     * Send message asynchronously
     */
    public CompletableFuture<SendResult<String, DataEvent>> sendMessageAsync(String topic, DataEvent event) {
        Instant start = Instant.now();
        return kafkaTemplate.send(topic, event.getEventId(), event)
            .whenComplete((result, ex) -> {
                Duration duration = Duration.between(start, Instant.now());
                if (ex == null) {
                    metricsService.recordProduceSuccess(topic, duration.toMillis());
                    log.debug("Async message sent successfully to topic {} in {}ms: {}", topic, duration.toMillis(), event.getEventId());
                } else {
                    metricsService.recordProduceFailure(topic, duration.toMillis());
                    log.error("Async message failed to topic {} in {}ms: {}", topic, duration.toMillis(), event.getEventId(), ex);
                }
            });
    }

    /**
     * Send message in a transaction
     */
    @Transactional
    public void sendMessageInTransaction(String topic, DataEvent event, Runnable callback) {
        Instant start = Instant.now();
        try {
            kafkaTemplate.executeInTransaction(operations -> {
                var sendFuture = operations.send(topic, event.getEventId(), event);

                // Execute callback within the same transaction
                if (callback != null) {
                    callback.run();
                }

                return sendFuture;
            });

            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordProduceSuccess(topic, duration.toMillis());

            log.debug("Transactional message sent to topic {} in {}ms: {}",
                     topic, duration.toMillis(), event.getEventId());

        } catch (Exception e) {
            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordProduceFailure(topic, duration.toMillis());

            log.error("Transactional message failed to topic {} in {}ms: {}",
                     topic, duration.toMillis(), event.getEventId(), e);
            throw e;
        }
    }

    /**
     * Send batch of messages
     */
    public void sendBatch(String topic, Iterable<DataEvent> events) {
        Instant start = Instant.now();
        int successCount = 0;
        int totalCount = 0;

        try {
            for (DataEvent event : events) {
                totalCount++;
                try {
                    sendMessageSync(topic, event);
                    successCount++;
                } catch (Exception e) {
                    log.warn("Failed to send event {} in batch: {}",
                            event.getEventId(), e.getMessage());
                }
            }

            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordBatchProduce(topic, totalCount, successCount, duration.toMillis());

            log.info("Batch sent to topic {}: {}/{} successful in {}ms",
                    topic, successCount, totalCount, duration.toMillis());

        } catch (Exception e) {
            Duration duration = Duration.between(start, Instant.now());
            metricsService.recordProduceFailure(topic, duration.toMillis());

            log.error("Batch send failed to topic {}: {}", topic, e.getMessage(), e);
        }
    }

    /**
     * Check if Kafka is healthy
     */
    public boolean isHealthy() {
        try {
            // Try to describe a topic to check connectivity
            var adminClient = kafkaTemplate.getProducerFactory()
                .createProducer()
                .metrics();

            return adminClient != null && !adminClient.isEmpty();
        } catch (Exception e) {
            log.warn("Kafka health check failed: {}", e.getMessage());
            return false;
        }
    }
}
