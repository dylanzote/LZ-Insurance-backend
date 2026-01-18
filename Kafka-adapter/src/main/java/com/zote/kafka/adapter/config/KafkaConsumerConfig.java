package com.zote.kafka.adapter.config;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@AllArgsConstructor
@Slf4j
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, DataEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties(null));
        // Configure JsonDeserializer for DataEvent
        JsonDeserializer<DataEvent> deserializer = new JsonDeserializer<>(DataEvent.class);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        // Update config with deserializers
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                  org.apache.kafka.common.serialization.StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                  JsonDeserializer.class);

        // Ensure JSON properties are set
        if (!config.containsKey(JsonDeserializer.TRUSTED_PACKAGES)) {
            config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        }
        if (!config.containsKey(JsonDeserializer.VALUE_DEFAULT_TYPE)) {
            config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DataEvent.class.getName());
        }

        // Set a unique client ID for monitoring
        config.put(ConsumerConfig.CLIENT_ID_CONFIG,
                  "zote-consumer-" + System.currentTimeMillis() + "-" +
                  Thread.currentThread().getId());

        log.debug("Consumer configuration: {}", config);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DataEvent> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DataEvent>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3);
         // Enable batch listening for high throughput
        factory.setBatchListener(false); // Set to true for batch processing

        // Configure error handler with retry
        factory.setCommonErrorHandler(errorHandler());

        // Set container properties
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(
            org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Set record interceptor for monitoring
        factory.setRecordInterceptor(new KafkaRecordInterceptor());
        return factory;
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        // Fixed backoff for blocking retries (before DLT)
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2L); // 1 second delay, 2 retries

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            (record, exception) -> {
                // This is called when all retries are exhausted
                log.error("Message processing failed after all retries. " +
                         "Topic: {}, Partition: {}, Offset: {}, Key: {}",
                         record.topic(), record.partition(),
                         record.offset(), record.key(), exception);

                // You can send to a custom DLT here
                // sendToDeadLetterTopic(record, exception);
            },
            fixedBackOff
        );

        // Configure which exceptions NOT to retry
        errorHandler.addNotRetryableExceptions(
            java.lang.IllegalArgumentException.class,
            org.springframework.kafka.support.serializer.DeserializationException.class
        );

        return errorHandler;
    }

    // Record interceptor for monitoring
    private static class KafkaRecordInterceptor implements RecordInterceptor<String, DataEvent> {
        @Override
        public ConsumerRecord<String, DataEvent> intercept(ConsumerRecord<String, DataEvent> record, Consumer<String, DataEvent> consumer) {
            log.debug("Processing record: Topic={}, Partition={}, Offset={}, Key={}", record.topic(), record.partition(), record.offset(), record.key());
            return record;
        }

        @Override
        public void success(ConsumerRecord<String, DataEvent> record, Consumer<String, DataEvent> consumer) {
            RecordInterceptor.super.success(record, consumer);
            log.debug("Successfully processed record: Key={}", record.key());
        }

        @Override
        public void failure(ConsumerRecord<String, DataEvent> record, Exception exception, Consumer<String, DataEvent> consumer) {
            RecordInterceptor.super.failure(record, exception, consumer);
            log.error("Failed to process record: Key={}", record.key(), exception);
        }
    }
}
