package com.zote.notification.service.api.controller;

import com.zote.notification.service.api.request.RegisterWebhookRequest;
import com.zote.notification.service.api.request.UpdateWebhookRequest;
import com.zote.notification.service.api.response.WebhookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Webhook API", description = "Webhook management endpoints for real-time notifications")
@RequestMapping("/notification/webhooks")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface WebhookApi {

    @Operation(summary = "Register a webhook endpoint")
    @PostMapping
    WebhookResponse registerWebhook(@Valid @RequestBody RegisterWebhookRequest request);

    @Operation(summary = "Get all webhooks for a user")
    @GetMapping("/user/{userId}")
    List<WebhookResponse> getUserWebhooks(@PathVariable("userId") String userId);

    @Operation(summary = "Get webhook by ID")
    @GetMapping("/{webhookId}")
    WebhookResponse getWebhook(@PathVariable("webhookId") String webhookId);

    @Operation(summary = "Update webhook")
    @PutMapping("/{webhookId}")
    WebhookResponse updateWebhook(
            @PathVariable("webhookId") String webhookId,
            @Valid @RequestBody UpdateWebhookRequest request);

    @Operation(summary = "Delete webhook")
    @DeleteMapping("/{webhookId}")
    void deleteWebhook(@PathVariable("webhookId") String webhookId);

    @Operation(summary = "Test webhook endpoint")
    @PostMapping("/{webhookId}/test")
    boolean testWebhook(@PathVariable("webhookId") String webhookId);

    @Operation(summary = "Get webhook delivery logs")
    @GetMapping("/{webhookId}/logs")
    List<Object> getWebhookLogs(
            @PathVariable("webhookId") String webhookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);
}

