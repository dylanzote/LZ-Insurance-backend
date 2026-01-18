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
public class UserGrowthStatsResponse {
    
    private int periodDays;
    private long totalNewUsers;
    private double averagePerDay;
    private List<DailyGrowth> dailyGrowth;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyGrowth {
        private LocalDate date;
        private long newUsers;
        private long cumulativeUsers;
    }
}

