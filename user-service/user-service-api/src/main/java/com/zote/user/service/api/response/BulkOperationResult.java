package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationResult {
    
    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<String> successfulUserIds;
    private Map<String, String> failures; // userId -> error message
}

