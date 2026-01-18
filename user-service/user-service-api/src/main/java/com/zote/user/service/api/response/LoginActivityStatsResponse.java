package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginActivityStatsResponse {
    
    private int periodDays;
    private long totalLogins;
    private long uniqueUsers;
    private double averageLoginsPerDay;
    private List<DailyLoginActivity> dailyActivity;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyLoginActivity {
        private LocalDate date;
        private long loginCount;
        private long uniqueUsers;
        private long failedAttempts;
    }
}

