package com.zote.keycloak.adapter.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String grantType;
    private String username;
    private String password;
    private String clientSecret;
    private String authServerUrl;
}
