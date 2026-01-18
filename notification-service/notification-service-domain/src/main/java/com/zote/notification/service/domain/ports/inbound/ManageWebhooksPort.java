package com.zote.notification.service.domain.ports.inbound;

import com.zote.notification.service.domain.model.Webhook;

import java.util.List;

public interface ManageWebhooksPort {
    
    Webhook registerWebhook(Webhook webhook);
    
    Webhook getWebhook(String webhookId);
    
    List<Webhook> getUserWebhooks(String userId);
    
    Webhook updateWebhook(String webhookId, Webhook webhook);
    
    void deleteWebhook(String webhookId);
    
    boolean testWebhook(String webhookId);
    
    void recordSuccess(String webhookId);
    
    void recordFailure(String webhookId, String error);
}

