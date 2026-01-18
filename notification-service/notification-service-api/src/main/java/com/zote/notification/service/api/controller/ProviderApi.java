package com.zote.notification.service.api.controller;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.api.request.CreateProviderRequest;
import com.zote.notification.service.api.request.UpdateProviderConfigRequest;
import com.zote.notification.service.api.request.UpdateProviderRequest;
import com.zote.notification.service.api.response.ProviderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Provider Management API")
@RequestMapping("/notification/providers")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface ProviderApi {

    @Operation(summary = "Create a provider")
    @PostMapping
    ProviderResponse createProvider(@Valid @RequestBody CreateProviderRequest request);

    @Operation(summary = "Update a provider")
    @PutMapping("/")
    ProviderResponse updateProvider(@Valid @RequestBody UpdateProviderRequest request);

    @Operation(summary = "Update a provider")
    @PutMapping("/{providerId}")
    ProviderResponse updateProviderConfig(@PathVariable("providerId")  String providerId, @Valid @RequestBody UpdateProviderConfigRequest request);

    @Operation(summary = "Delete a provider")
    @DeleteMapping("/{providerId}")
    void deleteProvider(@PathVariable("providerId")  String providerId);

    @Operation(summary = "Get all providers")
    @GetMapping
    List<ProviderResponse> getAllProviders();

    @Operation(summary = "Get providers by channel")
    @GetMapping("/channel/{channel}")
    List<ProviderResponse> getProvidersByChannel(@PathVariable("channel")  NotificationChannel channel);

    @Operation(summary = "Enable provider")
    @PutMapping("/{providerId}/enable")
    void enableProvider(@PathVariable("providerId")  String providerId);

    @Operation(summary = "Disable provider")
    @PutMapping("/{providerId}/disable")
    void disableProvider(@PathVariable("providerId")  String providerId);
}
