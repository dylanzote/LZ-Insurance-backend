package com.zote.common.utils.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("AuthEntryPoint triggered for request: " + request.getRequestURI());
        log.error(authException.getMessage(), authException);

        response.setContentType("application/json");

        if (authException instanceof BadCredentialsException) {
            log.error("Authentication failed: Bad credentials (wrong password).");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Invalid credentials.\" }");
        } else if (authException instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oauth2Ex = (OAuth2AuthenticationException) authException;
            String errorCode = oauth2Ex.getError().getErrorCode();
            log.error("OAuth2 authentication failed: Error code - " + errorCode);

            // Handle token-related errors
            if ("invalid_token".equals(errorCode)) {
                log.error("Invalid token detected.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Invalid token.\" }");
            } else if ("expired_token".equals(errorCode)) {
                log.error("Expired token detected.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{ \"status\": 401, \"error\": \"Token Expired\", \"message\": \"Your token has expired. Please re-authenticate.\" }");
            }
        } else if (authException instanceof InsufficientAuthenticationException) {
            log.error("Insufficient authentication: Full authentication is required.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Full authentication is required to access this resource.\" }");
        } else {
            log.error("Authentication error: " + authException.getMessage(), authException);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{ \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\" }");
        }

        response.getWriter().flush();
    }
}
