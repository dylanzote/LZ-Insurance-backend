package com.zote.notification.service.domain.ports.outbound.service;

import com.zote.common.utils.enums.DevicePlatform;

public interface TopicSubscriptionPort {
    void subscribeToTopic(String token, String topic, DevicePlatform platform);
    void unsubscribeFromTopic(String token, String topic, DevicePlatform platform);
}

