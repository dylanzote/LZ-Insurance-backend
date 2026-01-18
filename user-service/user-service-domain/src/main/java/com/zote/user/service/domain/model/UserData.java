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
public class UserData {

    String id;

    String firstName;

    String lastName;

    Gender gender;

    String email;

    String phoneNumber;

    String dateOfBirth;

    String town;

    Set<String> roleIds;

    String address;
    
    String branchId;
    
    String department;
    
    Language language;
    
    String twoFactorCode; // Optional: 2FA code for sensitive operations

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }

    public User toUser(User user) {
        BeanUtils.copyProperties(this, user);
        return user;
    }

}
