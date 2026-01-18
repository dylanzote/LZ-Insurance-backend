package com.zote.notification.service.domain.usecases;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.notification.service.domain.model.CreateProviderData;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.UpdateProviderData;
import com.zote.notification.service.domain.ports.inbound.ManageProvidersPort;
import com.zote.notification.service.domain.ports.outbound.repository.ProviderRepositoryPort;
import com.zote.notification.service.domain.support.ProviderSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ManageProvidersImpl implements ManageProvidersPort {

    private final ProviderRepositoryPort providerRepository;

    private final ProviderSupport providerSupport;

    @Override
    public NotificationProvider createProvider(CreateProviderData createProviderData) {
        log.info("Creating provider: {}", createProviderData.getName());

        var notificationProvider = providerRepository.findByName(createProviderData.getName());
        if (Objects.nonNull(notificationProvider)) {
            throw new FunctionalError("Provider with name already exists: " + createProviderData.getName());
        }
        log.info("Provider created successfully");
        return providerRepository.save(providerSupport.buildNotificationProvider(createProviderData));
    }

    @Override
    public NotificationProvider updateProvider(UpdateProviderData updateProviderData) {
        log.info("Updating provider: {}", updateProviderData.getProviderId());
        NotificationProvider provider = providerRepository.findById(updateProviderData.getProviderId());
        var updatedProvider = providerSupport.updateNotificationProvider(updateProviderData, provider);
        provider = providerRepository.save(updatedProvider);
        log.info("Provider updated successfully: {}", provider.getId());
        return provider;
    }

    @Override
    public void deleteProvider(String providerId) {
        log.info("Deleting provider: {}", providerId);

        NotificationProvider provider = providerRepository.findById(providerId);
        if (provider.getIsActive()) {
            throw new FunctionalError("Cannot delete an active provider. Please disable it first: " + providerId);
        }
        providerRepository.deleteById(providerId);
        log.info("Provider deleted successfully: {}", providerId);
    }

    @Override
    public List<NotificationProvider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public List<NotificationProvider> getProvidersByChannel(NotificationChannel channel) {
        return providerRepository.findByChannel(channel);
    }

    @Override
    public void enableProvider(String providerId) {
        log.info("Enabling provider: {}", providerId);
        NotificationProvider provider = providerRepository.findById(providerId);
        provider.setIsActive(true);
        providerRepository.save(provider);
        log.info("Provider enabled: {}", providerId);
    }

    @Override
    public void disableProvider(String providerId) {
        log.info("Disabling provider: {}", providerId);
        NotificationProvider provider = providerRepository.findById(providerId);
        provider.setIsActive(false);
        providerRepository.save(provider);
        log.info("Provider disabled: {}", providerId);
    }

    @Override
    public NotificationProvider updateProviderConfig(String providerId, Map<String, Object> config) {
        log.info("Updating provider config: {}", providerId);
        NotificationProvider provider = providerRepository.findById(providerId);
        provider.setConfig(config);
        return providerRepository.save(provider);
    }


}
