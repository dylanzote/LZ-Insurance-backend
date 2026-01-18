package com.zote.user.service.domain.usecase;

import com.zote.common.utils.enums.Status;
import com.zote.user.service.domain.model.*;
import com.zote.user.service.domain.ports.inbound.AnalyticsPort;
import com.zote.user.service.domain.ports.outbound.RoleRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserActivityRepositoryPort;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsImpl implements AnalyticsPort {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final UserActivityRepositoryPort activityRepository;

    @Override
    public AnalyticsDashboard getDashboardStats() {
        log.info("Calculating dashboard statistics");
        
        List<User> allUsers = userRepository.getAllUsers();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthAgo = now.minusMonths(1);
        LocalDateTime weekAgo = now.minusWeeks(1);
        LocalDateTime yesterday = now.minusDays(1);
        
        // Total counts by status
        long totalUsers = allUsers.size();
        long activeUsers = allUsers.stream()
                .filter(u -> u.getStatus() == Status.ACTIVE)
                .count();
        long suspendedUsers = allUsers.stream()
                .filter(u -> u.getStatus() == Status.SUSPENDED)
                .count();
        long deletedUsers = allUsers.stream()
                .filter(u -> u.getStatus() == Status.DELETED)
                .count();
        
        // Growth metrics
        long newUsersThisMonth = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(monthAgo))
                .count();
        
        long usersBeforeThisMonth = totalUsers - newUsersThisMonth;
        double userGrowthRate = usersBeforeThisMonth > 0 
                ? (newUsersThisMonth * 100.0) / usersBeforeThisMonth 
                : 0.0;
        
        // Login activity
        long usersLoggedInToday = allUsers.stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(yesterday))
                .count();
        long usersLoggedInThisWeek = allUsers.stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(weekAgo))
                .count();
        long usersLoggedInThisMonth = allUsers.stream()
                .filter(u -> u.getLastLogin() != null && u.getLastLogin().isAfter(monthAgo))
                .count();
        
        // 2FA metrics
        long usersWithTwoFactor = allUsers.stream()
                .filter(User::isTwoFactorEnabled)
                .count();
        double twoFactorAdoptionRate = totalUsers > 0 
                ? (usersWithTwoFactor * 100.0) / totalUsers 
                : 0.0;
        
        // Role distribution
        long customersCount = allUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> r.isCustomerRole()))
                .count();
        long agentsCount = allUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "AGENT".equalsIgnoreCase(r.getName())))
                .count();
        long adminsCount = allUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName())))
                .count();
        long systemUsersCount = allUsers.stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> 
                        !r.isCustomerRole() && !"AGENT".equalsIgnoreCase(r.getName()) && !"ADMIN".equalsIgnoreCase(r.getName())))
                .count();
        
        // Security metrics
        long accountsWithFailedLogins = allUsers.stream()
                .filter(u -> u.getFailedLoginAttempts() > 0)
                .count();
        long lockedAccounts = allUsers.stream()
                .filter(u -> u.getAccountLockedUntil() != null && u.getAccountLockedUntil().isAfter(now))
                .count();
        long accountsNeedingPasswordChange = allUsers.stream()
                .filter(User::isMustChangePassword)
                .count();
        
        // Department distribution
        int totalDepartments = (int) allUsers.stream()
                .map(User::getDepartment)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        
        // Recent activity
        long activitiesLast24Hours = activityRepository.countActivitiesSince(yesterday);
        
        return AnalyticsDashboard.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .suspendedUsers(suspendedUsers)
                .deletedUsers(deletedUsers)
                .newUsersThisMonth(newUsersThisMonth)
                .userGrowthRate(Math.round(userGrowthRate * 100.0) / 100.0)
                .usersLoggedInToday(usersLoggedInToday)
                .usersLoggedInThisWeek(usersLoggedInThisWeek)
                .usersLoggedInThisMonth(usersLoggedInThisMonth)
                .usersWithTwoFactor(usersWithTwoFactor)
                .twoFactorAdoptionRate(Math.round(twoFactorAdoptionRate * 100.0) / 100.0)
                .customersCount(customersCount)
                .agentsCount(agentsCount)
                .adminsCount(adminsCount)
                .systemUsersCount(systemUsersCount)
                .accountsWithFailedLogins(accountsWithFailedLogins)
                .lockedAccounts(lockedAccounts)
                .accountsNeedingPasswordChange(accountsNeedingPasswordChange)
                .totalDepartments(totalDepartments)
                .activitiesLast24Hours(activitiesLast24Hours)
                .build();
    }

    @Override
    public UserGrowthStats getUserGrowthStats(int days) {
        log.info("Calculating user growth stats for last {} days", days);
        
        List<User> allUsers = userRepository.getAllUsers();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // Group users by creation date
        Map<LocalDate, Long> usersByDate = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));
        
        // Calculate daily growth
        List<UserGrowthStats.DailyGrowth> dailyGrowth = new ArrayList<>();
        long cumulativeUsers = allUsers.stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().isBefore(startDate))
                .count();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            long newUsers = usersByDate.getOrDefault(date, 0L);
            cumulativeUsers += newUsers;
            
            dailyGrowth.add(UserGrowthStats.DailyGrowth.builder()
                    .date(date)
                    .newUsers(newUsers)
                    .cumulativeUsers(cumulativeUsers)
                    .build());
        }
        
        long totalNewUsers = dailyGrowth.stream()
                .mapToLong(UserGrowthStats.DailyGrowth::getNewUsers)
                .sum();
        double averagePerDay = totalNewUsers / (double) days;
        
        return UserGrowthStats.builder()
                .periodDays(days)
                .totalNewUsers(totalNewUsers)
                .averagePerDay(Math.round(averagePerDay * 100.0) / 100.0)
                .dailyGrowth(dailyGrowth)
                .build();
    }

    @Override
    public LoginActivityStats getLoginActivityStats(int days) {
        log.info("Calculating login activity stats for last {} days", days);
        
        List<User> allUsers = userRepository.getAllUsers();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // Group users by last login date
        Map<LocalDate, List<User>> loginsByDate = allUsers.stream()
                .filter(u -> u.getLastLogin() != null)
                .filter(u -> {
                    LocalDate loginDate = u.getLastLogin().toLocalDate();
                    return !loginDate.isBefore(startDate) && !loginDate.isAfter(endDate);
                })
                .collect(Collectors.groupingBy(
                        u -> u.getLastLogin().toLocalDate()
                ));
        
        // Calculate daily activity
        List<LoginActivityStats.DailyLoginActivity> dailyActivity = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<User> usersOnDate = loginsByDate.getOrDefault(date, Collections.emptyList());
            long loginCount = usersOnDate.size(); // Simplified - each user counted once
            long uniqueUsers = usersOnDate.size();
            long failedAttempts = usersOnDate.stream()
                    .mapToLong(User::getFailedLoginAttempts)
                    .sum();
            
            dailyActivity.add(LoginActivityStats.DailyLoginActivity.builder()
                    .date(date)
                    .loginCount(loginCount)
                    .uniqueUsers(uniqueUsers)
                    .failedAttempts(failedAttempts)
                    .build());
        }
        
        long totalLogins = dailyActivity.stream()
                .mapToLong(LoginActivityStats.DailyLoginActivity::getLoginCount)
                .sum();
        long uniqueUsers = loginsByDate.values().stream()
                .flatMap(List::stream)
                .map(User::getId)
                .distinct()
                .count();
        double averageLoginsPerDay = totalLogins / (double) days;
        
        return LoginActivityStats.builder()
                .periodDays(days)
                .totalLogins(totalLogins)
                .uniqueUsers(uniqueUsers)
                .averageLoginsPerDay(Math.round(averageLoginsPerDay * 100.0) / 100.0)
                .dailyActivity(dailyActivity)
                .build();
    }

    @Override
    public List<RoleDistribution> getRoleDistribution() {
        log.info("Calculating role distribution");
        
        List<User> allUsers = userRepository.getAllUsers();
        long totalUsers = allUsers.size();
        
        // Get all roles and count users for each
        return roleRepository.getAllRoles().stream()
                .map(role -> {
                    List<User> usersWithRole = allUsers.stream()
                            .filter(u -> u.getRoles().stream()
                                    .anyMatch(r -> r.getId().equals(role.getId())))
                            .toList();
                    
                    long userCount = usersWithRole.size();
                    double percentage = totalUsers > 0 
                            ? (userCount * 100.0) / totalUsers 
                            : 0.0;
                    long activeCount = usersWithRole.stream()
                            .filter(u -> u.getStatus() == Status.ACTIVE)
                            .count();
                    long suspendedCount = usersWithRole.stream()
                            .filter(u -> u.getStatus() == Status.SUSPENDED)
                            .count();
                    
                    return RoleDistribution.builder()
                            .roleName(role.getName())
                            .roleCode(role.getName())
                            .userCount(userCount)
                            .percentage(Math.round(percentage * 100.0) / 100.0)
                            .activeCount(activeCount)
                            .suspendedCount(suspendedCount)
                            .build();
                })
                .sorted(Comparator.comparingLong(RoleDistribution::getUserCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getDepartmentStats() {
        log.info("Calculating department statistics");
        
        List<User> allUsers = userRepository.getAllUsers();
        
        Map<String, List<User>> usersByDepartment = allUsers.stream()
                .filter(u -> u.getDepartment() != null)
                .collect(Collectors.groupingBy(User::getDepartment));
        
        return usersByDepartment.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("department", entry.getKey());
                    stat.put("userCount", entry.getValue().size());
                    stat.put("activeCount", entry.getValue().stream()
                            .filter(u -> u.getStatus() == Status.ACTIVE)
                            .count());
                    return stat;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Object getStatusBreakdown() {
        log.info("Calculating status breakdown");
        
        List<User> allUsers = userRepository.getAllUsers();
        
        Map<String, Long> statusCounts = allUsers.stream()
                .collect(Collectors.groupingBy(
                        u -> u.getStatus().name(),
                        Collectors.counting()
                ));
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("total", allUsers.size());
        breakdown.put("byStatus", statusCounts);
        
        return breakdown;
    }

    @Override
    public Object get2FAAdoptionRate() {
        log.info("Calculating 2FA adoption rate");
        
        List<User> allUsers = userRepository.getAllUsers();
        long total = allUsers.size();
        long with2FA = allUsers.stream()
                .filter(User::isTwoFactorEnabled)
                .count();
        
        Map<String, Object> adoption = new HashMap<>();
        adoption.put("totalUsers", total);
        adoption.put("usersWithTwoFactor", with2FA);
        adoption.put("usersWithoutTwoFactor", total - with2FA);
        adoption.put("adoptionRate", total > 0 
                ? Math.round((with2FA * 100.0 / total) * 100.0) / 100.0 
                : 0.0);
        
        return adoption;
    }

    @Override
    public List<Object> getActivityTrends(LocalDate startDate, LocalDate endDate) {
        log.info("Calculating activity trends from {} to {}", startDate, endDate);
        
        List<Object> trends = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            long activityCount = activityRepository.countActivitiesBetween(dayStart, dayEnd);
            
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", date);
            trend.put("activityCount", activityCount);
            trends.add(trend);
        }
        
        return trends;
    }
}

