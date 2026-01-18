package com.zote.user.service.api.request;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.user.service.domain.model.UserData;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Set;

public record UpdateUserRequest(
        @NotNull
        String id,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        Gender gender,
        @NotNull
        String email,
        @NotNull
        String phoneNumber,
        @NotNull
        String dateOfBirth,
        @NotNull
        String town,
        @NotNull
        Set<String> roleIds,
        @NotNull
        String address,
        String branchId,
        String department,
        Language language,
        String twoFactorCode  // Optional: 2FA code for sensitive operations
) {
        public UserData toUserUpdateData() {
                UserData userData = new UserData();
                BeanUtils.copyProperties(this, userData);
                return userData;
        }
}
