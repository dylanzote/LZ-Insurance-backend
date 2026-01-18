package com.zote.notification.service.api.usecase;

import com.zote.notification.service.api.controller.WebhookApi;
import com.zote.notification.service.api.request.RegisterWebhookRequest;
import com.zote.notification.service.api.request.UpdateWebhookRequest;
import com.zote.notification.service.api.response.WebhookResponse;
import com.zote.notification.service.domain.model.Webhook;
import com.zote.notification.service.domain.ports.inbound.ManageWebhooksPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class WebhookService implements WebhookApi {

    private final ManageWebhooksPort manageWebhooksPort;
    
    @Override
    public WebhookResponse registerWebhook(RegisterWebhookRequest request) {
        log.info("API: Registering webhook for user: {} with URL: {}", request.getUserId(), request.getUrl());
        
        Webhook webhook = Webhook.builder()
                .userId(request.getUserId())
                .url(request.getUrl())
                .description(request.getDescription())
                .eventTypes(request.getEventTypes())
                .secret(request.getSecret())
                .headers(request.getHeaders())
                .active(request.isActive())
                .build();
        
        Webhook created = manageWebhooksPort.registerWebhook(webhook);
        return toResponse(created);
    }

    @Override
    public List<WebhookResponse> getUserWebhooks(String userId) {
        log.info("API: Getting webhooks for user: {}", userId);
        return manageWebhooksPort.getUserWebhooks(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WebhookResponse getWebhook(String webhookId) {
        log.info("API: Getting webhook: {}", webhookId);
        return toResponse(manageWebhooksPort.getWebhook(webhookId));
    }

    @Override
    public WebhookResponse updateWebhook(String webhookId, UpdateWebhookRequest request) {
        log.info("API: Updating webhook: {}", webhookId);
        
        Webhook webhook = Webhook.builder()
                .url(request.getUrl())
                .description(request.getDescription())
                .eventTypes(request.getEventTypes())
                .secret(request.getSecret())
                .headers(request.getHeaders())
                .active(request.getActive() != null ? request.getActive() : false)
                .build();
        
        Webhook updated = manageWebhooksPort.updateWebhook(webhookId, webhook);
        return toResponse(updated);
    }

    @Override
    public void deleteWebhook(String webhookId) {
        log.info("API: Deleting webhook: {}", webhookId);
        manageWebhooksPort.deleteWebhook(webhookId);
    }

    @Override
    public boolean testWebhook(String webhookId) {
        log.info("API: Testing webhook: {}", webhookId);
        return manageWebhooksPort.testWebhook(webhookId);
    }

    @Override
    public List<Object> getWebhookLogs(String webhookId, int page, int size) {
        log.info("API: Getting webhook logs for: {} (page: {}, size: {})", webhookId, page, size);
        // TODO: Implement webhook logs (requires separate delivery log table)
        return new ArrayList<>();
    }
    
    private WebhookResponse toResponse(Webhook webhook) {
        return WebhookResponse.builder()
                .id(webhook.getId())
                .userId(webhook.getUserId())
                .url(webhook.getUrl())
                .description(webhook.getDescription())
                .eventTypes(webhook.getEventTypes())
                .active(webhook.isActive())
                .headers(webhook.getHeaders())
                .successCount(webhook.getSuccessCount())
                .failureCount(webhook.getFailureCount())
                .lastSuccessAt(webhook.getLastSuccessAt())
                .lastFailureAt(webhook.getLastFailureAt())
                .createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt())
                .build();
    }
}

