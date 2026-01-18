package com.zote.user.service.api.request;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import com.zote.user.service.domain.model.CreateUserData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;

import java.util.Set;

public record CreateUserRequest(
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
        @NotNull
        String password,
        String branchId,
        String department,
        Language language
) {

        public CreateUserData tocreateUserData() {
                CreateUserData createUserData = new CreateUserData();
                BeanUtils.copyProperties(this, createUserData);
                return createUserData;
        }

}
