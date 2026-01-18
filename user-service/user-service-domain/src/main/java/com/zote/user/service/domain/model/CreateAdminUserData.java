package com.zote.user.service.domain.model;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateAdminUserData {
    private String firstName;
    private String lastName;
    private String userName;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private Set<String> roleIds;
    private String dateOfBirth;
    private String town;
    private String address;
    private String branchId;
    private String department;
    private Language language;

    public CreateUserData tocreateUserData() {
        CreateUserData createUserData = new CreateUserData();
        BeanUtils.copyProperties(this, createUserData);
        return createUserData;
    }
}
