package com.zote.user.service.domain.model;

import com.zote.common.utils.enums.Gender;
import com.zote.common.utils.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserData {
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
    private String password;
    private String branchId;
    private String department;
    private Language language;
}
