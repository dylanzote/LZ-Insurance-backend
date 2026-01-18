package com.zote.user.service.domain.support;

import com.zote.common.utils.files.MinioObjectStorage;
import com.zote.user.service.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final MinioObjectStorage minioObjectStorage;

    public CompletableFuture<String> fetchUserImageAsync(User user) {
        return CompletableFuture
            .supplyAsync(() -> minioObjectStorage.getPresignedUrl(minioObjectStorage.getUserImageName(user.getId())))
                .exceptionally(ex -> {
                    log.error("Error fetching image URL for user {}: {}", user.getId(), ex.getMessage());
                    return null;
                });
    }

    public void updateUserLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
    }
}
