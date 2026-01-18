package com.zote.user.service.api.request;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.user.service.domain.model.CreateAdminUserData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Set;

public record CreateAdminUserRequest(
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        String userName,
        @NotNull
        Gender gender,
        @NotNull
        String email,
        @NotNull
        String phoneNumber,
        @NotNull
        Set<String> roleIds,
        @NotNull
        @Schema(description = "The user's date of birth in format dd/MM/yyyy", example = "15/05/1990")
        String dateOfBirth,
        @NotNull
        String town,
        @NotNull
        String address,
        String branchId,
        String department,
        Language language
) {
        public CreateAdminUserData toCreateAdminUserData() {
                CreateAdminUserData createAdminUserData = new CreateAdminUserData();
                BeanUtils.copyProperties(this, createAdminUserData);
                return createAdminUserData;
        }
}
