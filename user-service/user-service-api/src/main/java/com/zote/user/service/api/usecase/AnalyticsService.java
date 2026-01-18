package com.zote.user.service.api.usecase;

import com.zote.user.service.api.controller.AnalyticsApi;
import com.zote.user.service.api.response.AnalyticsDashboardResponse;
import com.zote.user.service.api.response.LoginActivityStatsResponse;
import com.zote.user.service.api.response.RoleDistributionResponse;
import com.zote.user.service.api.response.UserGrowthStatsResponse;
import com.zote.user.service.domain.ports.inbound.AnalyticsPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AnalyticsService implements AnalyticsApi {

    private final AnalyticsPort analyticsPort;

    @Override
    public AnalyticsDashboardResponse getDashboardStats() {
        log.info("Getting dashboard statistics");
        var domain = analyticsPort.getDashboardStats();
        return AnalyticsDashboardResponse.builder()
                .totalUsers(domain.getTotalUsers())
                .activeUsers(domain.getActiveUsers())
                .suspendedUsers(domain.getSuspendedUsers())
                .deletedUsers(domain.getDeletedUsers())
                .newUsersThisMonth(domain.getNewUsersThisMonth())
                .userGrowthRate(domain.getUserGrowthRate())
                .usersLoggedInToday(domain.getUsersLoggedInToday())
                .usersLoggedInThisWeek(domain.getUsersLoggedInThisWeek())
                .usersLoggedInThisMonth(domain.getUsersLoggedInThisMonth())
                .usersWithTwoFactor(domain.getUsersWithTwoFactor())
                .twoFactorAdoptionRate(domain.getTwoFactorAdoptionRate())
                .customersCount(domain.getCustomersCount())
                .agentsCount(domain.getAgentsCount())
                .adminsCount(domain.getAdminsCount())
                .systemUsersCount(domain.getSystemUsersCount())
                .accountsWithFailedLogins(domain.getAccountsWithFailedLogins())
                .lockedAccounts(domain.getLockedAccounts())
                .accountsNeedingPasswordChange(domain.getAccountsNeedingPasswordChange())
                .totalDepartments(domain.getTotalDepartments())
                .activitiesLast24Hours(domain.getActivitiesLast24Hours())
                .build();
    }

    @Override
    public UserGrowthStatsResponse getUserGrowthStats(int days) {
        log.info("Getting user growth stats for last {} days", days);
        var domain = analyticsPort.getUserGrowthStats(days);
        return UserGrowthStatsResponse.builder()
                .periodDays(domain.getPeriodDays())
                .totalNewUsers(domain.getTotalNewUsers())
                .averagePerDay(domain.getAveragePerDay())
                .dailyGrowth(domain.getDailyGrowth().stream()
                        .map(dg -> UserGrowthStatsResponse.DailyGrowth.builder()
                                .date(dg.getDate())
                                .newUsers(dg.getNewUsers())
                                .cumulativeUsers(dg.getCumulativeUsers())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public LoginActivityStatsResponse getLoginActivityStats(int days) {
        log.info("Getting login activity stats for last {} days", days);
        var domain = analyticsPort.getLoginActivityStats(days);
        return LoginActivityStatsResponse.builder()
                .periodDays(domain.getPeriodDays())
                .totalLogins(domain.getTotalLogins())
                .uniqueUsers(domain.getUniqueUsers())
                .averageLoginsPerDay(domain.getAverageLoginsPerDay())
                .dailyActivity(domain.getDailyActivity().stream()
                        .map(da -> LoginActivityStatsResponse.DailyLoginActivity.builder()
                                .date(da.getDate())
                                .loginCount(da.getLoginCount())
                                .uniqueUsers(da.getUniqueUsers())
                                .failedAttempts(da.getFailedAttempts())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<RoleDistributionResponse> getRoleDistribution() {
        log.info("Getting role distribution");
        return analyticsPort.getRoleDistribution().stream()
                .map(rd -> RoleDistributionResponse.builder()
                        .roleName(rd.getRoleName())
                        .roleCode(rd.getRoleCode())
                        .userCount(rd.getUserCount())
                        .percentage(rd.getPercentage())
                        .activeCount(rd.getActiveCount())
                        .suspendedCount(rd.getSuspendedCount())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getDepartmentStats() {
        log.info("Getting department statistics");
        return analyticsPort.getDepartmentStats();
    }

    @Override
    public Object getStatusBreakdown() {
        log.info("Getting status breakdown");
        return analyticsPort.getStatusBreakdown();
    }

    @Override
    public Object get2FAAdoptionRate() {
        log.info("Getting 2FA adoption rate");
        return analyticsPort.get2FAAdoptionRate();
    }

    @Override
    public List<Object> getActivityTrends(LocalDate startDate, LocalDate endDate) {
        log.info("Getting activity trends from {} to {}", startDate, endDate);
        return analyticsPort.getActivityTrends(startDate, endDate);
    }

    @Override
    public String exportAnalyticsCsv() {
        log.info("Exporting analytics to CSV");
        
        var dashboard = analyticsPort.getDashboardStats();
        var userGrowth = analyticsPort.getUserGrowthStats(30);
        var loginActivity = analyticsPort.getLoginActivityStats(7);
        
        StringBuilder csv = new StringBuilder();
        
        // Dashboard summary
        csv.append("=== Dashboard Summary ===\n");
        csv.append("Metric,Value\n");
        csv.append("Total Users,").append(dashboard.getTotalUsers()).append("\n");
        csv.append("Active Users,").append(dashboard.getActiveUsers()).append("\n");
        csv.append("Suspended Users,").append(dashboard.getSuspendedUsers()).append("\n");
        csv.append("Deleted Users,").append(dashboard.getDeletedUsers()).append("\n");
        csv.append("New Users This Month,").append(dashboard.getNewUsersThisMonth()).append("\n");
        csv.append("User Growth Rate,").append(dashboard.getUserGrowthRate()).append("%\n");
        csv.append("Users Logged In Today,").append(dashboard.getUsersLoggedInToday()).append("\n");
        csv.append("Users Logged In This Week,").append(dashboard.getUsersLoggedInThisWeek()).append("\n");
        csv.append("Users Logged In This Month,").append(dashboard.getUsersLoggedInThisMonth()).append("\n");
        csv.append("Users With 2FA,").append(dashboard.getUsersWithTwoFactor()).append("\n");
        csv.append("2FA Adoption Rate,").append(dashboard.getTwoFactorAdoptionRate()).append("%\n");
        csv.append("Locked Accounts,").append(dashboard.getLockedAccounts()).append("\n");
        csv.append("Accounts Needing Password Change,").append(dashboard.getAccountsNeedingPasswordChange()).append("\n");
        csv.append("\n");
        
        // User Growth
        csv.append("=== User Growth (Last 30 Days) ===\n");
        csv.append("Date,New Users\n");
        userGrowth.getDailyGrowth().forEach(growth -> 
            csv.append(growth.getDate()).append(",").append(growth.getNewUsers()).append("\n")
        );
        csv.append("\n");
        
        // Login Activity
        csv.append("=== Login Activity (Last 7 Days) ===\n");
        csv.append("Date,Login Count,Unique Users,Failed Attempts\n");
        loginActivity.getDailyActivity().forEach(activity -> 
            csv.append(activity.getDate()).append(",")
               .append(activity.getLoginCount()).append(",")
               .append(activity.getUniqueUsers()).append(",")
               .append(activity.getFailedAttempts()).append("\n")
        );
        
        return csv.toString();
    }
}

