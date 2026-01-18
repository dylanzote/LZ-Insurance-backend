package com.zote.keycloak.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserSession {
    private String id;
    private String username;
    private String userId;
    private String ipAddress;
    private long start;
    private long lastAccess;
    private boolean rememberMe;
    private Map<String, String> clients = new HashMap<>();
    private boolean transientUser;

    public static UserSession toUser(UserSessionRepresentation userRepresent) {
        var userSession = new UserSession();
        BeanUtils.copyProperties(userRepresent, userSession);
        return userSession;
    }
}
