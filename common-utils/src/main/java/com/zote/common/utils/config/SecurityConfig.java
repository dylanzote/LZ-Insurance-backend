package com.zote.common.utils.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConverter jwtConverter;

    private final AuthEntryPoint authEntryPoint;

    private final CustomAccessDenied accessDenied;

    @Autowired(required = false)
    private UserStatusValidationFilter userStatusValidationFilter;

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        // Configure OAuth2 resource server (this adds BearerTokenAuthenticationFilter internally)
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // disable to allow post and put request when permitted all is applied to apis with **
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(authEntryPoint);
                    exception.accessDeniedHandler(accessDenied);
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtConverter)));
        
        // Add user status validation filter after OAuth2 resource server authentication
        // This ensures JWT is validated first, then we check if user is suspended
        // The filter is added after BearerTokenAuthenticationFilter which is added by oauth2ResourceServer
        if (userStatusValidationFilter != null) {
            httpSecurity.addFilterAfter(userStatusValidationFilter, org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class);
        }
        
        return httpSecurity
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/v3/api-docs/swagger-config", "/swagger-ui/**", "/v3/**", "/v3/api-docs**").permitAll()
                        .requestMatchers("/user/create", "/user/get-all/role-name", "/authenticate", "/role/create", "/permission/create", "/messageTemplate/create", "/auth/**").permitAll()
                        .requestMatchers("/messageTemplate/create").permitAll()
                        .requestMatchers("/notification/**").permitAll()
                        .requestMatchers("/actuator/**", "/error", "/ws/**").permitAll()
                        .requestMatchers("/inspector/**").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/realms/real-estate}") String issuerUri) {
        String jwkSetUri = issuerUri + "/protocol/openid-connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
