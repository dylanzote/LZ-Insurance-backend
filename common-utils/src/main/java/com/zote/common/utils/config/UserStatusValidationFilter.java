package com.zote.common.utils.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zote.common.utils.enums.Status;
import com.zote.common.utils.utils.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Filter to validate that authenticated users are not suspended.
 * This prevents suspended users from accessing protected endpoints even if they have valid JWT tokens.
 * 
 * This filter runs after Spring Security authentication but before request handling,
 * ensuring that suspended users cannot access any protected resources.
 * 
 * The filter uses UserStatusValidationService which must be implemented by services that want to use this filter.
 */
@Component
@Slf4j
public class UserStatusValidationFilter extends OncePerRequestFilter {

    @Autowired(required = false)
    private Optional<UserStatusValidationService> userStatusValidationService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip if no UserStatusValidationService is available
        if (userStatusValidationService.isEmpty() || !userStatusValidationService.get().isAvailable()) {
            filterChain.doFilter(request, response);
            return;
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Only check for authenticated users (skip anonymous/unauthenticated requests and public endpoints)
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String keycloakUserId = SecurityUtils.getCurrentUsername();
                if (keycloakUserId != null && !keycloakUserId.equals("anonymousUser")) {
                    Status userStatus = userStatusValidationService.get().getUserStatus(keycloakUserId);
                    
                    if (userStatus == Status.SUSPENDED) {
                        log.warn("Suspended user (Keycloak ID: {}) attempted to access protected endpoint: {}", 
                                keycloakUserId, request.getRequestURI());
                        
                        // Clear security context to invalidate the session
                        SecurityContextHolder.clearContext();
                        
                        // Return 403 Forbidden with proper error message
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "ACCOUNT_SUSPENDED");
                        errorResponse.put("message", "Your account has been suspended. Please contact administrator.");
                        
                        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                        return;
                    }
                }
            } catch (Exception e) {
                // Log error but don't block request if user lookup fails
                // This could happen if user was deleted from DB but token is still valid
                // In that case, let Spring Security handle it
                log.error("Error checking user status in filter for request {}: {}", 
                        request.getRequestURI(), e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
