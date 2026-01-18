//package com.zote.apigateway;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//@EnableWebFluxSecurity  //That’s because Spring Cloud Gateway uses the reactive version of the Spring web module.
//@EnableMethodSecurity
//public class Config {
//
//    @Bean
//    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity httpSecurity) {
//        return httpSecurity
//                .authorizeExchange(authorize -> authorize
//                        .pathMatchers("/api/**", "/v3/api-docs", "/swagger-ui.html", "/v3/api-docs/swagger-config", "/user-service/swagger-ui/**").permitAll()
//                        .anyExchange().authenticated())
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))  //method verifies an access token before forwarding traffic to the downstream services
//                .build();
//    }
//}
