package com.zote.notification.service.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmailMessage {
    private List<String> to;
    private String subject;
    private String body;
    private boolean html;
    private List<Attachment> attachments;
}
