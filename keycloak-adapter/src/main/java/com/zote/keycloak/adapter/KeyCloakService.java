package com.zote.keycloak.adapter;

import com.zote.common.utils.exceptions.FunctionalError;
import com.zote.keycloak.adapter.model.KeyCloakUser;
import com.zote.keycloak.adapter.model.KeycloakProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class KeyCloakService {

    private final Keycloak keycloak;

    private final KeycloakProperties keycloakProperties;


    public List<KeyCloakUser> getUsers() {
        return keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .list()
                .stream()
                .map(KeyCloakUser::toUser)
                .collect(Collectors.toList());
    }

    public String createUser(KeyCloakUser keyCloakUser, String password) {
        keyCloakUser.setPassword(password);
        log.info("Creating keycloak user");
        var response = keycloak
               .realm(keycloakProperties.getRealm())
               .users()
               .create(keyCloakUser.toUserRepresentation());
        if (!Objects.equals(201, response.getStatus())) {
            throw new FunctionalError("user could not be created in Realm ");
        }

        return getUserRepresentation(keyCloakUser.getEmail()).getId();
    }

    public KeyCloakUser getUserById(String userId) {
        log.info("getting keycloak user with id {}", userId);
        UserResource userResource = getUserResource(userId);
        return KeyCloakUser.toUser(userResource.toRepresentation());
    }

    public UserRepresentation getUserRepresentation(String username) {
        return keycloak
                .realm(keycloakProperties.getRealm())
                .users().searchByUsername(username, true)
                .stream().findFirst().orElseThrow(() -> new FunctionalError("key clock user Not found"));
    }

    public List<UserSessionRepresentation> getUserSessions(String userId) {
        return keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .getUserSessions();
    }

    public void deleteUserSessions(String userId) {
        keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .get(userId)
                .logout();
        log.info("All sessions for user " + userId + " have been invalidated.");
    }



    public int getActiveSessionCount(String userId) {
        return getUserSessions(userId).size();
    }




    public void deleteUser(String userId) {
        log.info("Deleting keycloak user with id {}", userId);
        keycloak
               .realm(keycloakProperties.getRealm())
               .users()
               .delete(userId);
    }

    public void sendEmailVerification(String userId) {
        log.info("Sending email verification for user with id {}", userId);
        getUserResource(userId)
               .sendVerifyEmail();
    }

    public void assignRoleToUser(String userId, String roleName) {
        log.info("Assigning role {} to user with id {}", roleName, userId);
        var userResource = getUserResource(userId);
        var roleRepresentation = getRolesResource().get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
    }

    public void removeRoleToUser(String userId, String roleName) {
        log.info("Assigning role {} to user with id {}", roleName, userId);
        var userResource = getUserResource(userId);
        var oldRoleRepresentation = getRolesResource()
                .get(roleName)
                .toRepresentation();
        if (oldRoleRepresentation != null) {
            log.info("Removing old role '{}'", roleName);
            userResource.roles().realmLevel().remove(Collections.singletonList(oldRoleRepresentation));
        } else {
            log.warn("Old role '{}' not found for user '{}'", roleName, userId);
        }
    }

    public void resetPassword(String userId, String password) {
        log.info("Resetting password for user with id {}", userId);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        getUserResource(userId)
               .resetPassword(credentialRepresentation);
    }

    public void updateUser(KeyCloakUser keyCloakUser) {
        log.info("Updating keycloak user with id {}", keyCloakUser.getId());
        getUserResource(keyCloakUser.getId())
                .update(keyCloakUser.toUpdateUserRepresentation());
    }



    private UserResource getUserResource(String userId) {
        return keycloak
                .realm(keycloakProperties.getRealm())
                .users()
                .get(userId);
    }

    private RolesResource getRolesResource() {
        return keycloak
               .realm(keycloakProperties.getRealm())
               .roles();
    }

    public List<String> getRoles() {
        log.info("Getting all roles");
        return keycloak
                .realm(keycloakProperties.getRealm())
                .roles()
                .list()
                .stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    public void addRealmRole(String role, String description) {
//        deleteRealmRole(role);
        log.info("Adding realm role {}", role);
        if(!getRoles().contains(role)) {
            RoleRepresentation roleRep = new  RoleRepresentation();
            roleRep.setName(role);
            roleRep.setDescription(description);
            getRolesResource().create(roleRep);
        }
    }


    public void deleteRealmRole(String role) {
        log.info("Deleting realm role {}", role);
        getRolesResource().deleteRole(role);
    }

    public MultiValueMap<String, String> buildAuthRequest() {
        log.info("building authentication request data");
        MultiValueMap<String, String> loginData = new LinkedMultiValueMap<>();
        loginData.add("client_id", keycloakProperties.getClientId());
        loginData.add("client_secret", keycloakProperties.getClientSecret());
        loginData.add("grant_type", "client_credentials");
        return loginData;
    }

}
