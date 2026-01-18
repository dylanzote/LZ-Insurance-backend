package com.zote.notification.service.infrastructure.adapters.providers.email;

import com.zote.common.utils.enums.NotificationChannel;
import com.zote.common.utils.enums.ProviderType;
import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationResult;
import com.zote.notification.service.domain.model.ProviderHealth;
import com.zote.notification.service.domain.model.UserInfo;
import com.zote.notification.service.domain.ports.outbound.service.UserServicePort;
import com.zote.notification.service.infrastructure.adapters.providers.NotificationProviderAdapter;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notification.providers.smtp.enabled", havingValue = "true", matchIfMissing = false)
public class SendSmtpEmailProvider implements NotificationProviderAdapter {

    private final JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private UserServicePort userService;

    @Value("${notification.providers.smtp.mail.username:}")
    private String fromEmail;

    @Value("${notification.providers.smtp.mail.username:}")
    private String fromName;

    // Use the username as from email if no specific from-email is configured
    private String getFromEmail() {
        return fromEmail != null && !fromEmail.isEmpty() ? fromEmail : 
               (mailSender instanceof org.springframework.mail.javamail.JavaMailSenderImpl ? 
                ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getUsername() : fromEmail);
    }

    @Override
    public NotificationResult send(Notification notification) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("Sending email via SMTP to user: {}", notification.getUserId());

            String recipientEmail = getRecipientEmail(notification);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set from address
            String senderEmail = getFromEmail();
            helper.setFrom(senderEmail, fromName != null && !fromName.isEmpty() ? fromName : "LZ Insurance");
            
            // Set recipient
            helper.setTo(recipientEmail);
            
            // Set subject
            helper.setSubject(notification.getTitle());
            
            // Set body - use HTML content if available, otherwise plain text
            String htmlContent = getHtmlContent(notification);
            if (htmlContent != null && !htmlContent.isEmpty()) {
                // Set both plain text (fallback) and HTML
                helper.setText(notification.getMessage(), htmlContent);
                log.debug("Email set with HTML content (size: {} chars)", htmlContent.length());
            } else {
                // Plain text only
                helper.setText(notification.getMessage(), false);
                log.debug("Email set with plain text content");
            }

            // Add attachments if any
            if (notification.getMetadata() != null && notification.getMetadata().containsKey("attachments")) {
                Object attachmentsObj = notification.getMetadata().get("attachments");
                if (attachmentsObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> attachments = (Map<String, Object>) attachmentsObj;
                    addAttachments(helper, attachments);
                } else if (attachmentsObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> attachmentsList = (List<Map<String, Object>>) attachmentsObj;
                    addAttachmentsFromList(helper, attachmentsList);
                }
            }

            // Send email
            mailSender.send(message);

            long latencyMs = System.currentTimeMillis() - startTime;
            String messageId = message.getMessageID();

            log.info("Email sent successfully to {} in {}ms", recipientEmail, latencyMs);

            return NotificationResult.success(messageId).latencyMs(latencyMs);

        } catch (Exception e) {
            long latencyMs = System.currentTimeMillis() - startTime;
            log.error("Failed to send email via SMTP: {}", e.getMessage(), e);
            return NotificationResult.failed("SMTP error: " + e.getMessage()).latencyMs(latencyMs);
        }
    }

    /**
     * Get HTML content from notification metadata
     * This is set by the template renderer when rendering HTML templates
     */
    private String getHtmlContent(Notification notification) {
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("htmlContent")) {
            Object htmlObj = notification.getMetadata().get("htmlContent");
            if (htmlObj instanceof String) {
                return (String) htmlObj;
            }
        }
        return null;
    }

    /**
     * Get recipient email address
     * Priority: 1) metadata, 2) template variables, 3) user service
     */
    private String getRecipientEmail(Notification notification) {
        // First check metadata
        if (notification.getMetadata() != null && notification.getMetadata().containsKey("email")) {
            return (String) notification.getMetadata().get("email");
        }
        
        // Check template variables
        if (notification.getTemplateVariables() != null && notification.getTemplateVariables().containsKey("email")) {
            return (String) notification.getTemplateVariables().get("email");
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

    private void addAttachments(MimeMessageHelper helper, Map<String, Object> attachments) {
        try {
            attachments.forEach((name, data) -> {
                try {
                    if (data instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> attachmentData = (Map<String, Object>) data;
                        addSingleAttachment(helper, name, attachmentData);
                    } else if (data instanceof String) {
                        // Base64 encoded attachment
                        addBase64Attachment(helper, name, (String) data);
                    } else if (data instanceof byte[]) {
                        // Byte array attachment
                        addByteArrayAttachment(helper, name, (byte[]) data);
                    }
                } catch (Exception e) {
                    log.error("Failed to add attachment {}: {}", name, e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            log.error("Failed to process attachments: {}", e.getMessage(), e);
        }
    }

    private void addAttachmentsFromList(MimeMessageHelper helper, List<Map<String, Object>> attachmentsList) {
        for (Map<String, Object> attachment : attachmentsList) {
            try {
                String name = (String) attachment.getOrDefault("name", "attachment");
                Object content = attachment.get("content");
                String contentType = (String) attachment.getOrDefault("contentType", "application/octet-stream");

                if (content instanceof String) {
                    // Base64 encoded
                    addBase64Attachment(helper, name, (String) content, contentType);
                } else if (content instanceof byte[]) {
                    // Byte array
                    helper.addAttachment(name, new org.springframework.core.io.ByteArrayResource((byte[]) content), contentType);
                } else if (content instanceof Map) {
                    // Complex attachment data
                    @SuppressWarnings("unchecked")
                    Map<String, Object> attachmentData = (Map<String, Object>) content;
                    addSingleAttachment(helper, name, attachmentData);
                }
            } catch (Exception e) {
                log.error("Failed to add attachment from list: {}", e.getMessage(), e);
            }
        }
    }

    private void addSingleAttachment(MimeMessageHelper helper, String name, Map<String, Object> attachmentData) {
        try {
            Object content = attachmentData.get("content");
            String contentType = (String) attachmentData.getOrDefault("contentType", "application/octet-stream");
            String fileName = (String) attachmentData.getOrDefault("fileName", name);

            if (content instanceof String) {
                // Base64 encoded content
                addBase64Attachment(helper, fileName, (String) content, contentType);
            } else if (content instanceof byte[]) {
                // Byte array content
                helper.addAttachment(fileName, 
                    new org.springframework.core.io.ByteArrayResource((byte[]) content), 
                    contentType);
            } else {
                log.warn("Unsupported attachment content type for attachment: {}", name);
            }
        } catch (Exception e) {
            log.error("Failed to add single attachment {}: {}", name, e.getMessage(), e);
        }
    }

    private void addBase64Attachment(MimeMessageHelper helper, String name, String base64Content) {
        addBase64Attachment(helper, name, base64Content, "application/octet-stream");
    }

    private void addBase64Attachment(MimeMessageHelper helper, String name, String base64Content, String contentType) {
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Content);
            helper.addAttachment(name, 
                new org.springframework.core.io.ByteArrayResource(decodedBytes), 
                contentType);
            log.debug("Added base64 attachment: {} ({} bytes)", name, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            log.error("Invalid base64 content for attachment {}: {}", name, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to add base64 attachment {}: {}", name, e.getMessage(), e);
        }
    }

    private void addByteArrayAttachment(MimeMessageHelper helper, String name, byte[] content) {
        try {
            helper.addAttachment(name, 
                new org.springframework.core.io.ByteArrayResource(content), 
                "application/octet-stream");
            log.debug("Added byte array attachment: {} ({} bytes)", name, content.length);
        } catch (Exception e) {
            log.error("Failed to add byte array attachment {}: {}", name, e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "SMTP Email Service (Gmail)";
    }

    @Override
    public ProviderType getType() {
        return ProviderType.SMTP;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public boolean isConfigured() {
        return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
    }

    @Override
    public ProviderHealth healthCheck() {
        boolean isHealthy = isConfigured();
        return ProviderHealth.builder()
                .providerId("smtp")
                .providerName(getName())
                .isHealthy(isHealthy)
                .build();
    }

    @Override
    public void configure(Map<String, Object> config) {
        // Configuration is handled via Spring Mail properties
        log.debug("SMTP provider configuration updated");
    }
}
