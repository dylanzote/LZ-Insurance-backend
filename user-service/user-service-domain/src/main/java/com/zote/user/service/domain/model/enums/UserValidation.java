package com.zote.user.service.domain.model.enums;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.CreateUserData;

import java.util.Objects;

public enum UserValidation {
    PHONE_NUMBER {
        @Override
        public void validate(CreateUserData user) {
            if (Objects.isNull(user.getPhoneNumber())) {
                throw new FunctionalError("Phone number cannot be null");
            }
            if (!user.getPhoneNumber().matches("^[0-9]{9}$")) {
                throw new FunctionalError("phone number must contain 9 digits");
            }
            if (user.getPhoneNumber().startsWith("0")) {
                throw new FunctionalError("phone number cannot start with 0");
            }
        }
    }, EMAIL {
        @Override
        public void validate(CreateUserData user) {
            if (Objects.isNull(user.getEmail())) {
                throw new FunctionalError("Email cannot be null");
            }
            if (!user.getEmail().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
                throw new FunctionalError("Invalid email address format");
            }
        }
    }, PASSWORD_COMPLEXITY {
        @Override
        public void validate(CreateUserData user) {
            if (Objects.isNull(user.getPassword())) {
                throw new FunctionalError("Password cannot be null");
            }
            if (user.getPassword().length() < 8) {
                throw new FunctionalError("Password must be at least 8 characters long");
            }
            if (!user.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
                throw new FunctionalError("Password must contain at least one lowercase letter, one uppercase letter, one number and one special character");
            }
        }
    };

    public abstract void validate(CreateUserData user);
}
