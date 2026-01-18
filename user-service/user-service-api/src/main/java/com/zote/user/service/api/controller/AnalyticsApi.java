package com.zote.user.service.api.controller;

import com.zote.user.service.api.response.AnalyticsDashboardResponse;
import com.zote.user.service.api.response.UserGrowthStatsResponse;
import com.zote.user.service.api.response.LoginActivityStatsResponse;
import com.zote.user.service.api.response.RoleDistributionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Analytics API", description = "User analytics and statistics for admin dashboard")
@RequestMapping("/user/analytics")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public interface AnalyticsApi {

    @Operation(summary = "Get dashboard overview statistics")
    @GetMapping("/dashboard")
    AnalyticsDashboardResponse getDashboardStats();

    @Operation(summary = "Get user growth statistics")
    @GetMapping("/growth")
    UserGrowthStatsResponse getUserGrowthStats(@RequestParam(name = "days", defaultValue = "30") int days);

    @Operation(summary = "Get login activity statistics")
    @GetMapping("/login-activity")
    LoginActivityStatsResponse getLoginActivityStats(@RequestParam(name =  "days", defaultValue = "7") int days);

    @Operation(summary = "Get role distribution")
    @GetMapping("/role-distribution")
    List<RoleDistributionResponse> getRoleDistribution();

    @Operation(summary = "Get department statistics")
    @GetMapping("/departments")
    List<Object> getDepartmentStats();

    @Operation(summary = "Get user status breakdown")
    @GetMapping("/status-breakdown")
    Object getStatusBreakdown();

    @Operation(summary = "Get 2FA adoption rate")
    @GetMapping("/2fa-adoption")
    Object get2FAAdoptionRate();

    @Operation(summary = "Get user activity trends")
    @GetMapping("/activity-trends")
    List<Object> getActivityTrends(@RequestParam(name =  "startDate") LocalDate startDate,
                                   @RequestParam(name =  "endDate") LocalDate endDate);

    @Operation(summary = "Export all analytics to CSV")
    @GetMapping("/export")
    String exportAnalyticsCsv();
}

