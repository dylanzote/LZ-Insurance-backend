package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDashboardResponse {
    
    // Total counts
    private long totalUsers;
    private long activeUsers;
    private long suspendedUsers;
    private long deletedUsers;
    
    // Growth metrics (last 30 days)
    private long newUsersThisMonth;
    private double userGrowthRate; // Percentage
    
    // Activity metrics
    private long usersLoggedInToday;
    private long usersLoggedInThisWeek;
    private long usersLoggedInThisMonth;
    
    // 2FA metrics
    private long usersWithTwoFactor;
    private double twoFactorAdoptionRate; // Percentage
    
    // Role distribution
    private long customersCount;
    private long agentsCount;
    private long adminsCount;
    private long systemUsersCount;
    
    // Security metrics
    private long accountsWithFailedLogins;
    private long lockedAccounts;
    private long accountsNeedingPasswordChange;
    
    // Department distribution
    private int totalDepartments;
    
    // Recent activity
    private long activitiesLast24Hours;
}

