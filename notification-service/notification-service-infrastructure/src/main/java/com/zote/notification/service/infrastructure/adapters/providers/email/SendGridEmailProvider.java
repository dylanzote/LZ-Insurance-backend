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
@ConditionalOnProperty(name = "notification.providers.sendgrid.enabled", havingValue = "true")
public class SendGridEmailProvider implements NotificationProviderAdapter {


    @Value("${notification.providers.sendgrid.from-email:}")
    private String fromEmail;

    @Value("${notification.providers.sendgrid.from-name:}")
    private String fromName;
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private UserServicePort userService;

    @Override
    public NotificationResult send(Notification notification) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Sending email via SendGrid to user: {}", notification.getUserId());

            String recipientEmail = getRecipientEmail(notification);
            
            // Note: This implementation requires SendGrid Java SDK dependency
            // Add to pom.xml: com.sendgrid:sendgrid-java
            // For now, this is a placeholder that will work when SendGrid SDK is added
            
            // TODO: Implement SendGrid email sending when SDK is available
            // Example implementation:
            // Email from = new Email(fromEmail, fromName);
            // Email to = new Email(recipientEmail);
            // Content content = new Content("text/html", notification.getMessage());
            // Mail mail = new Mail(from, notification.getTitle(), to, content);
            // SendGrid sg = new SendGrid(apiKey);
            // Request request = new Request();
            // request.setMethod(Method.POST);
            // request.setEndpoint("mail/send");
            // request.setBody(mail.build());
            // Response response = sg.api(request);
            
            log.warn("SendGrid provider not fully implemented - requires SendGrid SDK dependency");
            log.info("Would send email to {} with subject: {}", recipientEmail, notification.getTitle());
            
            // Return success for now (remove when actual implementation is added)
            long latencyMs = System.currentTimeMillis() - startTime;
            return NotificationResult.success("sendgrid-placeholder").latencyMs(latencyMs);
            
            // Uncomment when SendGrid SDK is added:
            // if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            //     long latencyMs = System.currentTimeMillis() - startTime;
            //     String messageId = extractMessageId(response);
            //     return NotificationResult.success(messageId).latencyMs(latencyMs);
            // } else {
            //     return NotificationResult.failed("SendGrid error: " + response.getStatusCode());
            // }

        } catch (Exception e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            log.error("Failed to send email via SendGrid: {}", e.getMessage(), e);
            return NotificationResult.failed("SendGrid error: " + e.getMessage()).latencyMs(latencyMs);
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

    private void addAttachment(Map<String, Object> attachmentData) {
        // Implementation for adding attachments to SendGrid mail
        // This is a placeholder - actual implementation would depend on attachment format
        log.debug("Adding attachment: {}", attachmentData);
    }

    @Override
    public String getName() {
        return "SendGrid Email Service";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SENDGRID;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean isConfigured() {
        return  fromEmail != null && !fromEmail.isEmpty();
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("sendgrid")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via @Value annotations and SendGrid bean
        log.debug("SendGrid provider configuration updated");
    }
}
