package com.zote.user.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Component
public class ValidationService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
        "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[1-9][0-9]{8}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    );

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void validateEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new FunctionalError("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new FunctionalError("Invalid email address");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) {
            throw new FunctionalError("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new FunctionalError("Phone number must contain 9 digits and cannot start with 0");
        }
    }

    public void validatePassword(String password) {
        if (!StringUtils.hasText(password)) {
            throw new FunctionalError("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new FunctionalError("Password must be at least 8 characters long");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new FunctionalError("Password must contain at least one lowercase letter, " +
                                    "one uppercase letter, one number and one special character");
        }
    }

    public void validateDate(String date) {
        dateFormatter.parse(date);
    }
}
