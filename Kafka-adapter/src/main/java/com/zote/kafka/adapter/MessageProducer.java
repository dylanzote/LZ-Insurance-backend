package com.zote.kafka.adapter;

import com.zote.kafka.adapter.models.DataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final KafkaTemplate<String, DataEvent> kafkaTemplate;

    public void sendMessage(String topic, DataEvent event) {
        log.info("Producing message to topic {}: {}", topic, event);
        kafkaTemplate.send(topic, event.getEventId(), event);
    }
}
