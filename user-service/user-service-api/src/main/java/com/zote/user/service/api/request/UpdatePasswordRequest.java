package com.zote.user.service.api.request;

import com.zote.user.service.domain.model.CreateUserData;
import com.zote.user.service.domain.model.PasswordUpdateData;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

public record UpdatePasswordRequest(

        @NotNull
        String userId,

        @NotNull
        String oldPassword,

        @NotNull
        String newPassword,
        
        String twoFactorCode  // Optional: 2FA code for sensitive operations
) {
        public PasswordUpdateData toPasswordUpdateData() {
                PasswordUpdateData passwordUpdateData = new PasswordUpdateData();
                BeanUtils.copyProperties(this, passwordUpdateData);
                return passwordUpdateData;
        }
}
