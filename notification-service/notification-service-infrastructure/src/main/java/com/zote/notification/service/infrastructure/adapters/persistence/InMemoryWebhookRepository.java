package com.zote.notification.service.infrastructure.adapters.persistence;

import com.zote.notification.service.domain.model.Webhook;
import com.zote.notification.service.domain.ports.outbound.WebhookRepositoryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of WebhookRepositoryPort for MVP
 * TODO: Replace with JPA implementation for production
 */
@Repository
@Slf4j
public class InMemoryWebhookRepository implements WebhookRepositoryPort {

    private final Map<String, Webhook> storage = new ConcurrentHashMap<>();

    @Override
    public Webhook save(Webhook webhook) {
        log.debug("Saving webhook: {}", webhook.getId());
        storage.put(webhook.getId(), webhook);
        return webhook;
    }

    @Override
    public Optional<Webhook> findById(String id) {
        log.debug("Finding webhook by ID: {}", id);
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Webhook> findByUserId(String userId) {
        log.debug("Finding webhooks for user: {}", userId);
        return storage.values().stream()
                .filter(webhook -> webhook.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Webhook> findActiveWebhooksForEvent(String eventType) {
        log.debug("Finding active webhooks for event type: {}", eventType);
        return storage.values().stream()
                .filter(webhook -> webhook.isActive())
                .filter(webhook -> webhook.getEventTypes() == null || 
                                   webhook.getEventTypes().isEmpty() ||
                                   webhook.getEventTypes().contains(eventType))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting webhook: {}", id);
        storage.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }
}

