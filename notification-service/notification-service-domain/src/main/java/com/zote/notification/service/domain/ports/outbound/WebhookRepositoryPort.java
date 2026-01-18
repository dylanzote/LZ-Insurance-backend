package com.zote.notification.service.domain.ports.outbound;

import com.zote.notification.service.domain.model.Webhook;

import java.util.List;
import java.util.Optional;

public interface WebhookRepositoryPort {
    
    Webhook save(Webhook webhook);
    
    Optional<Webhook> findById(String id);
    
    List<Webhook> findByUserId(String userId);
    
    List<Webhook> findActiveWebhooksForEvent(String eventType);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}

