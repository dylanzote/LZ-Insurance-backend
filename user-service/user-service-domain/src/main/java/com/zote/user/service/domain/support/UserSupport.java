package com.zote.user.service.domain.support;

import com.zote.common.utils.config.BeanConfig;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.request.HttpService;
import com.zote.common.utils.utils.SecurityUtils;
import com.zote.keycloak.adapter.model.KeycloakProperties;
import com.zote.user.service.domain.model.*;
import com.zote.user.service.domain.ports.inbound.UserPort;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserSupport {

    private final BeanConfig config;

    private final HttpService httpService;

    private final KeycloakProperties keycloakProperties;

    private final UserRepositoryPort userRepositoryPort;

    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

    private static final String DIGITS = "0123456789";

    private static final String SPECIAL_CHARS = "!@#$%^&*()_-+=<>?";

    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;

    private static final int PASSWORD_LENGTH = 8;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public void validatePasswords(String password) {
        if (Objects.isNull(password)) {
            throw new FunctionalError("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new FunctionalError("Password must be at least 8 characters long");
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")) {
            throw new FunctionalError("Password must contain at least one lowercase letter, one uppercase letter, one number and one special character");
        }
    }

    public void validateEmail(String email) {
        if (Objects.isNull(email)) {
            throw new FunctionalError("Email cannot be empty");
        }
        if (!email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$")) {
            throw new FunctionalError("Invalid email address");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if (Objects.isNull(phoneNumber)) {
            throw new FunctionalError("Phone number cannot be null");
        }
        if (!phoneNumber.matches("^[0-9]{9}$")) {
            throw new FunctionalError("phone number must contain 9 digits");
        }
        if (phoneNumber.startsWith("0")) {
            throw new FunctionalError("phone number cannot start with 0");
        }
    }

    public void validateDate(String date) {
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            throw new FunctionalError("Invalid date");
        }
    }

    public void validateData(CreateUserData user) {
        log.info("validating user data");
        validateEmail(user.getEmail());
        validatePhoneNumber(user.getPhoneNumber());
        validatePasswords(user.getPassword());
        validateDate(user.getDateOfBirth());
    }

    public void validateData(CreateAdminUserData user) {
        log.info("validating user data");
        validateEmail(user.getEmail());
        validatePhoneNumber(user.getPhoneNumber());
        validateDate(user.getDateOfBirth());
    }

    public void validateData(UserData user) {
        log.info("validating user data");
        validateEmail(user.getEmail());
        validatePhoneNumber(user.getPhoneNumber());
    }

    public User buildUser(CreateUserData createUserData, Set<Role> roles) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .firstName(createUserData.getFirstName())
                .lastName(createUserData.getLastName())
                .userName(createUserData.getEmail())
                .email(createUserData.getEmail())
                .password(config.passwordEncoder().encode(createUserData.getPassword()))
                .roles(roles)
                .newUser(true)
                .address(createUserData.getAddress())
                .gender(createUserData.getGender())
                .dateOfBirth(createUserData.getDateOfBirth())
                .phoneNumber(createUserData.getPhoneNumber())
                .status(Status.INACTIVE)
                .town(createUserData.getTown())
                .branchId(createUserData.getBranchId())
                .department(createUserData.getDepartment())
                .build();
    }


    public MultiValueMap<String, String> buildAuthRequest(String username, String password) {
        log.info("building authentication request data");
        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("client_id", keycloakProperties.getClientId());
        loginData.add("client_secret", keycloakProperties.getClientSecret());
        loginData.add("grant_type", keycloakProperties.getGrantType());
        loginData.add("username", username);
        loginData.add("password", password);
        return loginData;
    }

    public MultiValueMap<String, String> buildRefreshRequest(String refreshToken) {
        log.info("building token refresh request data");
        MultiValueMap<String, String> refreshData = new LinkedMultiValueMap<>();
        refreshData.add("client_id", keycloakProperties.getClientId());
        refreshData.add("client_secret", keycloakProperties.getClientSecret());
        refreshData.add("grant_type", "refresh_token");
        refreshData.add("refresh_token", refreshToken);
        return refreshData;
    }

    public AuthData authenticateKeycloakUser(MultiValueMap<String, String> authData) {
        log.info("authenticating user from keycloak");
        return httpService.post(keycloakProperties.getAuthServerUrl(), authData, AuthData.class);
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALL_CHARACTERS.length());
            password.append(ALL_CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public User getCurrentUser() {
        log.info("Fetching current authenticated user");
        var keycloakUserId = SecurityUtils.getCurrentUsername();
        return userRepositoryPort.findUserByKeyCloakId(keycloakUserId);
    }

}
