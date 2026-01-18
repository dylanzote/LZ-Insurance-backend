package com.zote.notification.service.api.usecase;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.notification.service.api.controller.ProviderApi;
import com.zote.notification.service.api.request.CreateProviderRequest;
import com.zote.notification.service.api.request.UpdateProviderConfigRequest;
import com.zote.notification.service.api.request.UpdateProviderRequest;
import com.zote.notification.service.api.response.ProviderResponse;
import com.zote.notification.service.domain.ports.inbound.ManageProvidersPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProviderService implements ProviderApi {

    private final ManageProvidersPort manageProvidersPort;

    @Override
    public ProviderResponse createProvider(CreateProviderRequest request) {
        log.info("API: Creating provider: {}", request.getName());
        return ProviderResponse.toResponse(manageProvidersPort.createProvider(request.toCreateProviderData()));
    }

    @Override
    public ProviderResponse updateProvider(UpdateProviderRequest request) {
        log.info("API: Updating provider: {}", request.getProviderId());
        return ProviderResponse.toResponse(manageProvidersPort.updateProvider(request.toUpdateProviderData()));
    }

    @Override
    public ProviderResponse updateProviderConfig(String providerId, UpdateProviderConfigRequest request) {
        log.info("API: Updating provider config: {}", providerId);
        return ProviderResponse.toResponse(manageProvidersPort.updateProviderConfig(providerId, request.getConfig()));
    }

    @Override
    public void deleteProvider(String providerId) {
        log.info("API: Deleting provider: {}", providerId);
        manageProvidersPort.deleteProvider(providerId);
    }

    @Override
    public List<ProviderResponse> getAllProviders() {
        log.info("API: Getting all providers");
        return manageProvidersPort.getAllProviders().stream()
            .map(ProviderResponse::toResponse)
            .toList();

    }

    @Override
    public List<ProviderResponse> getProvidersByChannel(NotificationChannel channel) {
        log.info("API: Getting providers for channel: {}", channel);
        return manageProvidersPort.getProvidersByChannel(channel).stream()
            .map(ProviderResponse::toResponse)
            .toList();
    }

    @Override
    public void enableProvider(String providerId) {
        log.info("API: Enabling provider: {}", providerId);
        manageProvidersPort.enableProvider(providerId);
    }

    @Override
    public void disableProvider(String providerId) {
        log.info("API: Disabling provider: {}", providerId);
        manageProvidersPort.disableProvider(providerId);
    }
}
