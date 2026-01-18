package com.zote.user.service.infrastructure.outbound.persistence.port;

import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserActivity;
import com.zote.user.service.domain.ports.outbound.UserActivityRepositoryPort;
import com.zote.user.service.infrastructure.outbound.entities.UserActivityEntity;
import com.zote.user.service.infrastructure.outbound.entities.UserEntity;
import com.zote.user.service.infrastructure.outbound.persistence.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserActivityRepositoryPortImpl implements UserActivityRepositoryPort {

    private final UserActivityRepository userActivityRepository;

    @Override
    public UserActivity saveActivity(UserActivity activity, User user) {
        log.info("Saving user activity: {}", activity);
        UserActivityEntity entity = UserActivityEntity.toEntity(activity, UserEntity.toEntity(user));
        return userActivityRepository.save(entity).toDto();
    }

    @Override
    public List<UserActivity> findActivitiesByUserId(String userId) {
        log.info("Finding activities for user: {}", userId);
        return userActivityRepository.findActivitiesByUserId(userId).stream()
                .map(UserActivityEntity::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserActivity> findAllActivities() {
        log.info("Finding all activities");
        return userActivityRepository.findAllActivitiesOrderByCreatedAtDesc().stream()
                .map(UserActivityEntity::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public long countActivitiesSince(LocalDateTime since) {
        log.info("Counting activities since: {}", since);
        return userActivityRepository.countByCreatedAtAfter(since);
    }

    @Override
    public long countActivitiesBetween(LocalDateTime start, LocalDateTime end) {
        log.info("Counting activities between: {} and {}", start, end);
        return userActivityRepository.countByCreatedAtBetween(start, end);
    }
}

