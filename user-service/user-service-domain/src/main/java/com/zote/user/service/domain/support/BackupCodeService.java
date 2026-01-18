package com.zote.user.service.domain.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.zote.common.utils.config.BeanConfig;
import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.common.utils.utils.JsonUtil;
import com.zote.user.service.domain.model.User;
import com.zote.user.service.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupCodeService {

     private final UserRepositoryPort userRepositoryPort;
    private final BeanConfig beanConfig;
    private final ActivityLogger activityLogger;

    private static final String BACKUP_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int BACKUP_CODE_LENGTH = 8;
    private static final int BACKUP_CODE_COUNT = 10;

    @Transactional
    public List<String> generateBackupCodes(String userId) {
        log.info("Generating backup codes for user: {}", userId);

        User user = findUserById(userId);
        validateTwoFactorEnabled(user);

        List<String> backupCodes = generateUniqueCodes();
        storeBackupCodes(user, backupCodes);

        logActivity(userId, SecurityConstants.ACTIVITY_GENERATE_BACKUP_CODES);
        log.info("Generated {} backup codes for user: {}", backupCodes.size(), userId);

        return backupCodes;
    }

    public boolean verifyBackupCode(String userId, String code) {
        log.info("Verifying backup code for user: {}", userId);

        User user = findUserById(userId);

        if (!hasBackupCodes(user)) {
            log.warn("No backup codes found for user: {}", userId);
            return false;
        }

        try {
            List<String> hashedCodes = getHashedBackupCodes(user);
            int matchedIndex = findMatchingCodeIndex(code, hashedCodes);

            if (matchedIndex >= 0) {
                removeUsedBackupCode(user, hashedCodes, matchedIndex);
                logActivity(userId, SecurityConstants.ACTIVITY_USE_BACKUP_CODE, "Remaining: " + (hashedCodes.size() - 1));
                return true;
            }

            log.warn("Invalid backup code for user: {}", userId);
            return false;

        } catch (Exception e) {
            log.error("Failed to verify backup code for user: {}", userId, e);
            throw new FunctionalError(SecurityConstants.ERROR_BACKUP_CODE_VERIFICATION_FAILED);
        }
    }

    @Transactional
    public boolean verifyBackupCodeForCurrentUser(User currentUser, String code) {
        return verifyBackupCode(currentUser.getId(), code);
    }

    public int getRemainingBackupCodesCount(String userId) {
        log.info("Getting remaining backup codes count for user: {}", userId);

        User user = findUserById(userId);

        if (!hasBackupCodes(user)) {
            return 0;
        }

        try {
            List<String> hashedCodes = getHashedBackupCodes(user);
            return hashedCodes.size();
        } catch (Exception e) {
            log.error("Failed to get backup codes count for user: {}", userId, e);
            return 0;
        }
    }

    public int getRemainingBackupCodesCountForCurrentUser(User currentUser) {
        return getRemainingBackupCodesCount(currentUser.getId());
    }

    @Transactional
    public void revokeAllBackupCodes(String userId) {
        log.info("Revoking all backup codes for user: {}", userId);

        User user = findUserById(userId);
        user.setTwoFactorBackupCodes(null);
        userRepositoryPort.saveUser(user);

        logActivity(userId, SecurityConstants.ACTIVITY_REVOKE_BACKUP_CODES);
        log.info("All backup codes revoked for user: {}", userId);
    }


    private User findUserById(String userId) {
        return userRepositoryPort.findUserById(userId);
    }

    private void validateTwoFactorEnabled(User user) {
        if (!user.isTwoFactorEnabled()) {
            throw new FunctionalError(SecurityConstants.ERROR_2FA_NOT_ENABLED_FOR_BACKUP_CODES);
        }
    }

    private boolean hasBackupCodes(User user) {
        return user.getTwoFactorBackupCodes() != null &&
               !user.getTwoFactorBackupCodes().isEmpty();
    }

    private List<String> generateUniqueCodes() {
        List<String> codes = new ArrayList<>();
        Random random = new SecureRandom();

        for (int i = 0; i < BACKUP_CODE_COUNT; i++) {
            codes.add(generateRandomCode(random));
        }

        return codes;
    }

    private String generateRandomCode(Random random) {
        StringBuilder code = new StringBuilder(BACKUP_CODE_LENGTH);
        for (int i = 0; i < BACKUP_CODE_LENGTH; i++) {
            int index = random.nextInt(BACKUP_CODE_CHARS.length());
            code.append(BACKUP_CODE_CHARS.charAt(index));
        }
        return code.toString();
    }

    private void storeBackupCodes(User user, List<String> plainCodes) {
        List<String> hashedCodes = hashBackupCodes(plainCodes);
        String jsonCodes = serializeBackupCodes(hashedCodes);

        user.setTwoFactorBackupCodes(jsonCodes);
        userRepositoryPort.saveUser(user);
    }

    private List<String> hashBackupCodes(List<String> plainCodes) {
        return plainCodes.stream()
                .map(s -> beanConfig.passwordEncoder().encode(s))
                .toList();
    }

    private String serializeBackupCodes(List<String> hashedCodes) {
        return JsonUtil.serialize(hashedCodes);
    }

    private List<String> getHashedBackupCodes(User user)  {
        return JsonUtil.deserialize(user.getTwoFactorBackupCodes(), new TypeReference<List<String>>() {}
        );
    }

    private int findMatchingCodeIndex(String code, List<String> hashedCodes) {
        for (int i = 0; i < hashedCodes.size(); i++) {
            if (beanConfig.passwordEncoder().matches(code, hashedCodes.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private void removeUsedBackupCode(User user, List<String> hashedCodes, int usedIndex) {
        hashedCodes.remove(usedIndex);
        String updatedJson = JsonUtil.serialize(hashedCodes);
        user.setTwoFactorBackupCodes(updatedJson);
        userRepositoryPort.saveUser(user);
    }


    private void logActivity(String userId, String activity) {
        logActivity(userId, activity, null);
    }

    private void logActivity(String userId, String activity, String details) {
        activityLogger.logActivity(userId, activity, SecurityConstants.MODULE_PROFILE, details);
    }


    public boolean isValidBackupCodeFormat(String code) {
        if (code == null || code.length() != BACKUP_CODE_LENGTH) {
            return false;
        }

        for (char c : code.toCharArray()) {
            if (BACKUP_CODE_CHARS.indexOf(c) == -1) {
                return false;
            }
        }

        return true;
    }

    public boolean hasValidBackupCodes(User user) {
        if (!hasBackupCodes(user)) {
            return false;
        }

        try {
            List<String> hashedCodes = getHashedBackupCodes(user);
            return !hashedCodes.isEmpty();
        } catch (Exception e) {
            log.error("Failed to check backup codes validity for user: {}", user.getId(), e);
            return false;
        }
    }
}
