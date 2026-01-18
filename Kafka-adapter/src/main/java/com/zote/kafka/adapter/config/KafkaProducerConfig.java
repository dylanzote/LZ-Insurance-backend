package com.zote.kafka.adapter.config;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@AllArgsConstructor
@Slf4j
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, DataEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties(null));


        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

//        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
//                  kafkaProperties.getProducer().getProperties().get("max.in.flight.requests.per.connection"));
//        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,
//                  kafkaProperties.getProducer().getProperties().get("enable.idempotence"));
//        config.put(ProducerConfig.LINGER_MS_CONFIG,
//                  kafkaProperties.getProducer().getProperties().get("linger.ms"));
        if (kafkaProperties.getProducer().getProperties() != null) {
            config.putAll(kafkaProperties.getProducer().getProperties());
        }

        // Monitoring
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "zote-producer-" + System.currentTimeMillis()+ "-" +
                  Thread.currentThread().getId());
        log.info("Producer configuration: {}", config);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, DataEvent> kafkaTemplate() {
        KafkaTemplate<String, DataEvent> template = new KafkaTemplate<>(producerFactory());

        // Enable transaction support if needed
        template.setTransactionIdPrefix("zote-tx-");

        // Set producer listener for callbacks
        template.setProducerListener(new KafkaProducerListener());

        return template;
    }

    @Bean
    public RetryTemplate kafkaRetryTemplate() {
        return RetryTemplate.builder()
            .maxAttempts(3)
            .fixedBackoff(1000) // 1 second between retries
            .retryOn(KafkaException.class)
            .build();
    }

    private static class KafkaProducerListener implements ProducerListener<String, DataEvent> {
        @Override
        public void onSuccess(ProducerRecord<String, DataEvent> result, RecordMetadata metadata) {
            log.debug("Message sent successfully to topic {} with offset {}", metadata.topic(), metadata.offset());
        }

        @Override
        public void onError(ProducerRecord<String, DataEvent> record, RecordMetadata metadata, Exception exception) {
            log.error("Failed to send message to topic {}: {}", record.topic(), exception.getMessage(), exception);
        }
    }

}
