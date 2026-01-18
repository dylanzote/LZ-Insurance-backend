package com.zote.keycloak.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KeyCloakUser {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private String password;

    public UserRepresentation toUserRepresentation() {
        List<CredentialRepresentation> credentialRepresentations = new ArrayList<>();
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentations.add(credentialRepresentation);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(email);
        userRepresentation.setUsername(userName);
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setCredentials(credentialRepresentations);
        return userRepresentation;
    }

    public UserRepresentation toUpdateUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmail(email);
        userRepresentation.setUsername(userName);
        return userRepresentation;
    }

    public static KeyCloakUser toUser(UserRepresentation userRepresent) {
        KeyCloakUser keyCloakUser = new KeyCloakUser();
        BeanUtils.copyProperties(userRepresent, keyCloakUser);
        return keyCloakUser;
    }
}


