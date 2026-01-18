package com.zote.user.service.domain.support;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.user.service.domain.model.AuthData;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImpersonationService {

    private final UserRepositoryPort userRepositoryPort;
    private final UserSupport userSupport;
    private final TokenService tokenService;
    private final ActivityLogger activityLogger;

    @Transactional
    public AuthData impersonateUser(String targetUserId) {
        log.info("Impersonation request: {}",  targetUserId);

        var currentUser = userSupport.getCurrentUser();
        var targetUser = findUser(targetUserId);

        validateImpersonation(currentUser, targetUser);
        generateImpersonationToken(currentUser, targetUser);
        AuthData authData = createImpersonationSession(targetUser);
        logImpersonationActivities(currentUser, targetUser);

        return authData;
    }

    private User findUser(String userId) {
        return userRepositoryPort.findUserById(userId);
    }

    private void validateImpersonation(User currentUser, User targetUser) {
        validateImpersonationPermission(currentUser);
        preventSelfImpersonation(currentUser, targetUser);
        validatePrivilegeHierarchy(currentUser, targetUser);
    }

    private void validateImpersonationPermission(User user) {
        if (!hasImpersonationPermission(user)) {
            log.warn("User {} lacks impersonation permission", user.getId());
            throw new FunctionalError(SecurityConstants.ERROR_IMPERSONATION_PERMISSION_DENIED);
        }
    }

    private boolean hasImpersonationPermission(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> !role.isCustomerRole());
    }

    private void preventSelfImpersonation(User currentUser, User targetUser) {
        if (currentUser.getId().equals(targetUser.getId())) {
            log.warn("Self-impersonation attempt by {}", currentUser.getId());
            throw new FunctionalError(SecurityConstants.ERROR_SELF_IMPERSONATION);
        }
    }

    private void validatePrivilegeHierarchy(User currentUser, User targetUser) {
        if (isAdminUser(targetUser) && !isAdminUser(currentUser)) {
            log.warn("Privilege escalation attempt: {} -> {}", currentUser.getId(), targetUser.getId());
            throw new FunctionalError(SecurityConstants.ERROR_PRIVILEGE_ESCALATION);
        }
    }

    private boolean isAdminUser(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> !role.isCustomerRole());
    }

    private void generateImpersonationToken(User currentUser, User targetUser) {
        tokenService.generateImpersonationToken(targetUser, currentUser.getId());
    }

    private AuthData createImpersonationSession(User targetUser) {
        var authRequest = userSupport.buildAuthRequest(targetUser.getEmail(), targetUser.getPassword());
        AuthData authData = userSupport.authenticateKeycloakUser(authRequest);
        authData.setUser(targetUser);
        return authData;
    }

    private void logImpersonationActivities(User currentUser, User targetUser) {
        logActivity(currentUser, SecurityConstants.ACTIVITY_IMPERSONATE, targetUser.getId());
        logActivity(targetUser, SecurityConstants.ACTIVITY_IMPERSONATED_BY, currentUser.getId());
    }

    private void logActivity(User user, String action, String resourceId) {
        activityLogger.logActivity(
            user.getId(),
            action,
            SecurityConstants.MODULE_AUTH,
            resourceId
        );
    }
}
