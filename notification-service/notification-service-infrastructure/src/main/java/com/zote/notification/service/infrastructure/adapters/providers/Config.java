package com.zote.notification.service.infrastructure.adapters.providers;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class Config {

    @Value("${notification.providers.fcm.credentials-file:lz-insurance-firebase.json}")
    private String firebaseConfigPath;

    @Bean
    @ConditionalOnProperty(name = "notification.providers.fcm.enabled", havingValue = "true", matchIfMissing = false)
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    @ConditionalOnProperty(name = "notification.providers.fcm.enabled", havingValue = "true", matchIfMissing = false)
    FirebaseApp firebaseApp(GoogleCredentials credentials) {
        // Check if FirebaseApp already exists (e.g., from previous initialization or DevTools restart)
        // This prevents "FirebaseApp name [DEFAULT] already exists!" error during hot reload
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        
        // FirebaseApp doesn't exist yet, initialize it
        FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(credentials)
          .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    @ConditionalOnProperty(name = "notification.providers.fcm.enabled", havingValue = "true", matchIfMissing = false)
    @SneakyThrows
    GoogleCredentials googleCredentials() {
        var googleCredential =  GoogleCredentials.fromStream(new ClassPathResource("/initial-data/".concat(firebaseConfigPath)).getInputStream());
        if (googleCredential != null) {
            return googleCredential;
        }
        else {
            // Use standard credentials chain. Useful when running inside GKE
            return GoogleCredentials.getApplicationDefault();
        }
    }
}
