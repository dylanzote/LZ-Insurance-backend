package com.zote.notification.service.infrastructure.adapters.providers.sms;

import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.infrastructure.adapters.providers.NotificationProviderAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.providers.twilio.enabled", havingValue = "true")
public class TwilioSmsProvider implements NotificationProviderAdapter {

    private final TwilioRestClient twilioClient;

    @Value("${notification.providers.twilio.from-phone-number:}")
    private String fromPhoneNumber;

    @Override
    public NotificationResult send(Notification notification) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(getRecipientPhone(notification)),
                    new PhoneNumber(fromPhoneNumber),
                    notification.getMessage()
            ).create(twilioClient);

            return NotificationResult.success(message.getSid());
        } catch (ApiException e) {
            log.error("Twilio error", e);
            return NotificationResult.failed("Twilio error: " + e.getMessage());
        }
    }

    private String getRecipientPhone(Notification notification) {
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("phoneNumber")) {
            return (String) notification.getMetadata().get("phoneNumber");
        }
        // In a real system, you would fetch user's phone from user service
        throw new IllegalArgumentException("Recipient phone number not found in notification metadata");
    }

    @Override
    public String getName() {
        return "Twilio SMS Service";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.TWILIO;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public boolean isConfigured() {
        return twilioClient != null && fromPhoneNumber != null && !fromPhoneNumber.isEmpty();
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("twilio")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via @Value annotations and Twilio bean
        log.debug("Twilio provider configuration updated");
    }
}
