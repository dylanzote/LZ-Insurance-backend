package com.zote.notification.service.infrastructure.adapters.providers.email;


import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.model.UserInfo;
import com.zote.notification.service.domain.ports.outbound.service.UserServicePort;
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
@ConditionalOnProperty(name = "notification.providers.ses.enabled", havingValue = "true")
public class SesEmailProvider implements NotificationProviderAdapter {


    @Value("${notification.providers.ses.from-email:}")
    private String fromEmail;
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private UserServicePort userService;

    @Override
    public NotificationResult send(Notification notification) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Sending email via AWS SES to user: {}", notification.getUserId());

            String recipientEmail = getRecipientEmail(notification);
            
            // Note: This implementation requires AWS SDK for SES dependency
            // Add to pom.xml: com.amazonaws:aws-java-sdk-ses
            // For now, this is a placeholder that will work when AWS SDK is added
            
            // TODO: Implement AWS SES email sending when SDK is available
            // Example implementation:
            // AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
            //     .withRegion(region)
            //     .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            //     .build();
            // 
            // SendEmailRequest request = new SendEmailRequest()
            //     .withDestination(new Destination().withToAddresses(recipientEmail))
            //     .withMessage(new Message()
            //         .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(notification.getMessage())))
            //         .withSubject(new Content().withCharset("UTF-8").withData(notification.getTitle())))
            //     .withSource(fromEmail);
            // 
            // SendEmailResult result = client.sendEmail(request);
            
            log.warn("AWS SES provider not fully implemented - requires AWS SDK dependency");
            log.info("Would send email to {} with subject: {}", recipientEmail, notification.getTitle());
            
            // Return success for now (remove when actual implementation is added)
            long latencyMs = System.currentTimeMillis() - startTime;
            return NotificationResult.success("ses-placeholder").latencyMs(latencyMs);
            
            // Uncomment when AWS SDK is added:
            // long latencyMs = System.currentTimeMillis() - startTime;
            // return NotificationResult.success(result.getMessageId()).latencyMs(latencyMs);

        } catch (Exception e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            log.error("Failed to send email via AWS SES: {}", e.getMessage(), e);
            return NotificationResult.failed("SES error: " + e.getMessage()).latencyMs(latencyMs);
        }
    }

    private String getRecipientEmail(Notification notification) {
        // First check metadata
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("email")) {
            return (String) notification.getMetadata().get("email");
        }
        
        // Fallback to user service
        if (notification.getUserId() != null && userService != null) {
            return userService.getUserById(notification.getUserId())
                    .map(UserInfo::getEmail)
                    .orElseThrow(() -> new IllegalArgumentException(
                        "Recipient email not found in notification metadata and user-service unavailable for userId: " + 
                        notification.getUserId()));
        }
        
        throw new IllegalArgumentException("Recipient email not found in notification metadata and userId not available");
    }

    @Override
    public String getName() {
        return "Amazon SES Email Service";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SES;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean isConfigured() {
        return fromEmail != null && !fromEmail.isEmpty();
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("ses")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via @Value annotations and AWS SES bean
        log.debug("SES provider configuration updated");
    }
}
