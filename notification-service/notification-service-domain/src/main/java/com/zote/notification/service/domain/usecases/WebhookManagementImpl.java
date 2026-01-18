package com.zote.notification.service.domain.usecases;

import com.zote.notification.service.domain.model.Webhook;
import com.zote.notification.service.domain.ports.inbound.ManageWebhooksPort;
import com.zote.notification.service.domain.ports.outbound.WebhookRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookManagementImpl implements ManageWebhooksPort {

    private final WebhookRepositoryPort webhookRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Webhook registerWebhook(Webhook webhook) {
        log.info("Registering webhook for user: {} with URL: {}", webhook.getUserId(), webhook.getUrl());
        
        // Validate
        if (webhook.getUrl() == null || !webhook.getUrl().startsWith("https://")) {
            throw new IllegalArgumentException("Webhook URL must use HTTPS");
        }
        
        // Set defaults
        webhook.setId(UUID.randomUUID().toString());
        webhook.setActive(true);
        webhook.setSuccessCount(0);
        webhook.setFailureCount(0);
        webhook.setCreatedAt(LocalDateTime.now());
        webhook.setUpdatedAt(LocalDateTime.now());
        webhook.setCreatedBy(webhook.getUserId());
        
        return webhookRepository.save(webhook);
    }

    @Override
    public Webhook getWebhook(String webhookId) {
        log.info("Getting webhook: {}", webhookId);
        return webhookRepository.findById(webhookId)
                .orElseThrow(() -> new RuntimeException("Webhook not found: " + webhookId));
    }

    @Override
    public List<Webhook> getUserWebhooks(String userId) {
        log.info("Getting all webhooks for user: {}", userId);
        return webhookRepository.findByUserId(userId);
    }

    @Override
    public Webhook updateWebhook(String webhookId, Webhook webhook) {
        log.info("Updating webhook: {}", webhookId);
        
        Webhook existing = getWebhook(webhookId);
        
        // Update fields
        if (webhook.getUrl() != null) {
            if (!webhook.getUrl().startsWith("https://")) {
                throw new IllegalArgumentException("Webhook URL must use HTTPS");
            }
            existing.setUrl(webhook.getUrl());
        }
        if (webhook.getDescription() != null) {
            existing.setDescription(webhook.getDescription());
        }
        if (webhook.getEventTypes() != null) {
            existing.setEventTypes(webhook.getEventTypes());
        }
        if (webhook.getSecret() != null) {
            existing.setSecret(webhook.getSecret());
        }
        if (webhook.getHeaders() != null) {
            existing.setHeaders(webhook.getHeaders());
        }
        if (webhook.isActive() != existing.isActive()) {
            existing.setActive(webhook.isActive());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(webhook.getUserId());
        
        return webhookRepository.save(existing);
    }

    @Override
    public void deleteWebhook(String webhookId) {
        log.info("Deleting webhook: {}", webhookId);
        webhookRepository.deleteById(webhookId);
    }

    @Override
    public boolean testWebhook(String webhookId) {
        log.info("Testing webhook: {}", webhookId);
        
        try {
            Webhook webhook = getWebhook(webhookId);
            
            // Send test payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("event", "webhook.test");
            payload.put("webhookId", webhookId);
            payload.put("timestamp", LocalDateTime.now().toString());
            payload.put("message", "This is a test notification from LZ Insurance");
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("X-LZ-Event", "webhook.test");
            
            // Add custom headers
            if (webhook.getHeaders() != null) {
                webhook.getHeaders().forEach(headers::set);
            }
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    webhook.getUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            
            if (success) {
                recordSuccess(webhookId);
                log.info("Webhook test successful: {}", webhookId);
            } else {
                recordFailure(webhookId, "HTTP " + response.getStatusCode());
                log.warn("Webhook test failed: {} - HTTP {}", webhookId, response.getStatusCode());
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Webhook test failed: {}", webhookId, e);
            recordFailure(webhookId, e.getMessage());
            return false;
        }
    }

    @Override
    public void recordSuccess(String webhookId) {
        try {
            Webhook webhook = getWebhook(webhookId);
            webhook.setSuccessCount(webhook.getSuccessCount() + 1);
            webhook.setLastSuccessAt(LocalDateTime.now());
            webhook.setUpdatedAt(LocalDateTime.now());
            webhookRepository.save(webhook);
        } catch (Exception e) {
            log.error("Failed to record webhook success: {}", webhookId, e);
        }
    }

    @Override
    public void recordFailure(String webhookId, String error) {
        try {
            Webhook webhook = getWebhook(webhookId);
            webhook.setFailureCount(webhook.getFailureCount() + 1);
            webhook.setLastFailureAt(LocalDateTime.now());
            webhook.setLastError(error);
            webhook.setUpdatedAt(LocalDateTime.now());
            
            // Auto-disable after 10 consecutive failures
            if (webhook.getFailureCount() >= 10 && 
                (webhook.getLastSuccessAt() == null || 
                 webhook.getLastSuccessAt().isBefore(webhook.getLastFailureAt()))) {
                webhook.setActive(false);
                log.warn("Webhook {} auto-disabled after 10 failures", webhookId);
            }
            
            webhookRepository.save(webhook);
        } catch (Exception e) {
            log.error("Failed to record webhook failure: {}", webhookId, e);
        }
    }
}

