package com.zote.notification.service.infrastructure.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.TopicManagementResponse;
import com.zote.common.utils.enums.DevicePlatform;
import com.zote.notification.service.domain.ports.outbound.service.TopicSubscriptionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Service for managing push notification topic subscriptions
 * Supports FCM (Firebase Cloud Messaging) and Expo
 */
@Service
@Slf4j
public class TopicSubscriptionService implements TopicSubscriptionPort {

    private final FirebaseMessaging firebaseMessaging; // Optional, injected if FCM is enabled

    @Autowired(required = false)
    public TopicSubscriptionService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }


    /**
     * Subscribe a token to a topic based on platform
     */
    public void subscribeToTopic(String token, String topic, DevicePlatform platform) {
        if (token == null || topic == null) {
            log.warn("Cannot subscribe: token or topic is null");
            return;
        }

        try {
            if (isExpoPlatform(platform)) {
                subscribeExpoToTopic(token, topic);
            } else if (isFcmPlatform(platform)) {
                subscribeFcmToTopic(token, topic);
            } else {
                log.debug("Topic subscription not supported for platform: {}", platform);
            }
        } catch (Exception e) {
            log.error("Error subscribing token {} to topic {} on platform {}", token, topic, platform, e);
        }
    }

    /**
     * Unsubscribe a token from a topic based on platform
     */
    public void unsubscribeFromTopic(String token, String topic, DevicePlatform platform) {
        if (token == null || topic == null) {
            log.warn("Cannot unsubscribe: token or topic is null");
            return;
        }

        try {
            if (isExpoPlatform(platform)) {
                unsubscribeExpoFromTopic(token, topic);
            } else if (isFcmPlatform(platform)) {
                unsubscribeFcmFromTopic(token, topic);
            } else {
                log.debug("Topic unsubscription not supported for platform: {}", platform);
            }
        } catch (Exception e) {
            log.error("Error unsubscribing token {} from topic {} on platform {}", token, topic, platform, e);
        }
    }

    /**
     * Subscribe FCM token to topic using Firebase Admin SDK
     */
    private void subscribeFcmToTopic(String token, String topic) {
        if (firebaseMessaging == null) {
            log.debug("FirebaseMessaging not available, skipping FCM topic subscription");
            return;
        }

        try {
            TopicManagementResponse response = firebaseMessaging.subscribeToTopic(
                    Collections.singletonList(token),
                    topic
            );

            if (response.getSuccessCount() > 0) {
                log.debug("Successfully subscribed FCM token to topic: {}", topic);
            }

            if (response.getFailureCount() > 0) {
                log.warn("Failed to subscribe {} tokens to topic {}",
                        response.getFailureCount(), topic);
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM topic subscription error", e);
        }
    }

    /**
     * Unsubscribe FCM token from topic using Firebase Admin SDK
     */
    private void unsubscribeFcmFromTopic(String token, String topic) {
        if (firebaseMessaging == null) {
            log.debug("FirebaseMessaging not available, skipping FCM topic unsubscription");
            return;
        }

        try {
            TopicManagementResponse response = firebaseMessaging.unsubscribeFromTopic(
                    Collections.singletonList(token),
                    topic
            );

            if (response.getSuccessCount() > 0) {
                log.debug("Successfully unsubscribed FCM token from topic: {}", topic);
            }

            if (response.getFailureCount() > 0) {
                log.warn("Failed to unsubscribe {} tokens from topic {}",
                        response.getFailureCount(), topic);
            }
        } catch (FirebaseMessagingException e) {
            log.error("FCM topic unsubscription error", e);
        }
    }

    /**
     * Subscribe Expo token to topic
     * Note: Expo doesn't have native topic support, but we can track it in our database
     * For actual Expo push, we'll filter by topics when sending
     */
    private void subscribeExpoToTopic(String token, String topic) {
        // Expo doesn't have server-side topic subscription API
        // Topics are managed in our database and used for filtering when sending
        log.debug("Expo topic subscription tracked in database for token: {}, topic: {}", token, topic);
    }

    /**
     * Unsubscribe Expo token from topic
     */
    private void unsubscribeExpoFromTopic(String token, String topic) {
        // Expo doesn't have server-side topic unsubscription API
        log.debug("Expo topic unsubscription tracked in database for token: {}, topic: {}", token, topic);
    }

    private boolean isExpoPlatform(DevicePlatform platform) {
        return platform == DevicePlatform.EXPO_IOS ||
                platform == DevicePlatform.EXPO_ANDROID ||
                platform == DevicePlatform.EXPO_WEB;
    }

    private boolean isFcmPlatform(DevicePlatform platform) {
        return platform == DevicePlatform.ANDROID ||
                platform == DevicePlatform.IOS ||
                platform == DevicePlatform.WEB;
    }

}

