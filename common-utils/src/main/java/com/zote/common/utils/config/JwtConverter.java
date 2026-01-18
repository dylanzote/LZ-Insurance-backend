package com.zote.common.utils.config;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {


    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private static final String ROLE_PREFIX = "ROLE_";
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Combine authorities from default converter and resource roles
        Collection<GrantedAuthority> authorities = Stream
                .concat(getJwtAuthorities(jwt).stream(),
                        extractResourceRoles(jwt).stream())
                .collect(Collectors.toSet());
        // Create and return the authentication token
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaim(jwt));
    }

    private Collection<GrantedAuthority> getJwtAuthorities(Jwt jwt) {
        return Optional.ofNullable(jwtGrantedAuthoritiesConverter.convert(jwt))
                .orElse(Collections.emptyList());
    }

    private String getPrincipalClaim(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) {
            return Collections.emptySet();  // Return empty set if no resource access is found
        }

        List<String> realmRoles = (List<String>) realmAccess.get("roles");
        if (CollectionUtils.isNotEmpty(realmRoles)) {
            return realmRoles.stream()
                    .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
