package com.zote.user.service.domain.ports.inbound;

import com.zote.user.service.domain.model.AnalyticsDashboard;
import com.zote.user.service.domain.model.LoginActivityStats;
import com.zote.user.service.domain.model.RoleDistribution;
import com.zote.user.service.domain.model.UserGrowthStats;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsPort {
    
    AnalyticsDashboard getDashboardStats();
    
    UserGrowthStats getUserGrowthStats(int days);
    
    LoginActivityStats getLoginActivityStats(int days);
    
    List<RoleDistribution> getRoleDistribution();
    
    List<Object> getDepartmentStats();
    
    Object getStatusBreakdown();
    
    Object get2FAAdoptionRate();
    
    List<Object> getActivityTrends(LocalDate startDate, LocalDate endDate);
}

