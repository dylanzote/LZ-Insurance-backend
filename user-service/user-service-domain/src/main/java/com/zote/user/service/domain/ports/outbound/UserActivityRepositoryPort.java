package com.zote.user.service.domain.ports.outbound;

import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.model.UserActivity;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityRepositoryPort {
    
    UserActivity saveActivity(UserActivity activity, User user);
    
    List<UserActivity> findActivitiesByUserId(String userId);
    
    List<UserActivity> findAllActivities();
    
    long countActivitiesSince(LocalDateTime since);
    
    long countActivitiesBetween(LocalDateTime start, LocalDateTime end);
}

