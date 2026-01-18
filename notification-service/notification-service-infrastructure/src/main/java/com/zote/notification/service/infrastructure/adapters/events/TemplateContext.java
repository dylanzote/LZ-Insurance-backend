package com.zote.notification.service.infrastructure.adapters.events;

import com.zote.kafka.adapter.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemplateContext {

    @Value("${notification.kafka.user-service-topic:user-events}")
    private String userServiceTopic;

    @Value("${notification.admin-portal.url}")
    private String adminUrlHost;

    @Value("${notification.admin-portal.password-reset-path}")
    private String adminUrlPath;

    @Value("${notification.admin-portal.privacy-policy-path}")
    private String adminUrlPolicyPath;

    @Value("${notification.admin-portal.terms-path}")
    private String adminTermPath;

    public Map<String, Object> userCreated(UserCreatedEvent event) {
        Map<String, Object> ctx = baseUserContext(event);
        addSystemConstants(ctx);
        return ctx;
    }

    public Map<String, Object> adminCreatedUser(UserCreatedEvent event) {
        Map<String, Object> ctx = baseUserContext(event);
        String passwordResetUrl = adminUrlHost.concat(adminUrlPath).concat("?token=").concat(event.getPasswordResetToken());

        ctx.put("passwordResetUrl", passwordResetUrl);
        ctx.put("passwordResetToken", event.getPasswordResetToken());
        ctx.put("tokenExpiryHours", event.getPasswordResetTokenExpiryTime());

        addFooter(ctx);
        addSystemConstants(ctx);

        return ctx;
    }

    public Map<String, Object> eventMetadata(UserCreatedEvent event) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("eventId", event.getEventId());
        metadata.put("correlationId", event.getCorrelationId());
        metadata.put("eventType", event.getEventType());
        metadata.put("occurredAt", event.getOccurredAt());
        if (event.isCreatedByAdmin()) {
                metadata.put("createdByAdmin", true);
                metadata.put("createdByUserId", event.getCreatedByUserId());
            }
        return metadata;
    }

    private Map<String, Object> baseUserContext(UserCreatedEvent event) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("firstName", event.getFirstName());
        ctx.put("lastName", event.getLastName());
        ctx.put("email", event.getEmail());
        ctx.put("userId", event.getUserId());
        ctx.put("phoneNumber", event.getPhoneNumber());
        ctx.put("branchId", event.getBranchId());
        ctx.put("department", event.getDepartment());
        ctx.put("language", event.getLanguage() != null ? event.getLanguage().getCode() : "en");
        return ctx;
    }

    private void addSystemConstants(Map<String, Object> ctx) {
        ctx.put("companyName", "LZ Insurance");
        ctx.put("supportEmail", "support@lz-insurance.com");
        ctx.put("websiteUrl", "https://lz-insurance.com");
        ctx.put("currentYear", LocalDate.now().getYear());
    }

    private void addFooter(Map<String, Object> ctx) {
        ctx.put("privacyPolicyUrl", adminUrlHost + adminUrlPolicyPath);
        ctx.put("termsUrl", adminUrlHost + adminTermPath);
    }
}
