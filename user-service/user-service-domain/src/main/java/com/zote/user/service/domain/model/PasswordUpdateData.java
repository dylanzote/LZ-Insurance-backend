package com.zote.user.service.domain.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PasswordUpdateData {

    String userId;

    String oldPassword;

    String newPassword;
    
    String twoFactorCode; // Optional: 2FA code for sensitive operations
}
