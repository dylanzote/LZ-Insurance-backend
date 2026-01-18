package com.zote.user.service.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordStrengthResponse {
    
    private boolean valid;
    private String strength; // WEAK, MEDIUM, STRONG, VERY_STRONG
    private int score; // 0-100
    private List<String> suggestions;
    private List<String> requirements;
    private boolean meetsMinimumRequirements;
}

