package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.notification.service.domain.model.Notification;
import com.zote.notification.service.domain.model.NotificationProvider;
import com.zote.notification.service.domain.model.NotificationResult;

public interface PushSender {
    NotificationResult send(Notification notification, NotificationProvider provider);
}
