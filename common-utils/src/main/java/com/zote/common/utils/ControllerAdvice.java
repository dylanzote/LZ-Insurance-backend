package com.zote.common.utils;

import com.zote.common.utils.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(value = FunctionalError.class)
    public ErrorMessage wrongCredentialException(final FunctionalError e) {
        // Special handling for password change required
        if ("PASSWORD_CHANGE_REQUIRED".equals(e.getMessage())) {
            log.warn("User must change password before accessing the system");
            return new ErrorMessage(FORBIDDEN.value(), e.getMessage());
        }
        // Special handling for account suspended
        if ("ACCOUNT_SUSPENDED".equals(e.getMessage())) {
            log.warn("Suspended user attempted to access the system");
            return new ErrorMessage(FORBIDDEN.value(), e.getMessage());
        }
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = AgentNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage wrongCredentialException(final AgentNotFoundException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = UnAuthorizedException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorMessage wrongCredentialException(final UnAuthorizedException e) {
        return new ErrorMessage(UNAUTHORIZED.value(), e.getMessage());
    }

    @ExceptionHandler(value = DuplicateNotificationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage wrongCredentialException(final DuplicateNotificationException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = RateLimitExceededException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage wrongCredentialException(final RateLimitExceededException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = ProviderNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleProviderNotFoundException(final ProviderNotFoundException e) {
        return new ErrorMessage(NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = ProviderNotConfiguredException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleProviderNotConfiguredException(final ProviderNotConfiguredException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = DeviceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleDeviceNotFoundException(final DeviceNotFoundException e) {
        return new ErrorMessage(NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = NoAvailableProviderException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleNoAvailableProviderException(final NoAvailableProviderException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = NotificationNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleNotificationNotFoundException(final NotificationNotFoundException e) {
        return new ErrorMessage(NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = InvalidTemplateVariableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleInvalidTemplateVariableException(final InvalidTemplateVariableException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = MissingTemplateVariableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleMissingTemplateVariableException(final MissingTemplateVariableException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = TemplateRenderException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleTemplateRenderException(final TemplateRenderException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = TemplateNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleTemplateNotFoundException(final TemplateNotFoundException e) {
        return new ErrorMessage(NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(value = QuietHoursException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleQuietHoursException(final QuietHoursException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = ChannelDisabledException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorMessage handleChannelDisabledException(final ChannelDisabledException e) {
        return new ErrorMessage(BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorMessage handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorMessage(NOT_FOUND.value(), e.getMessage());
    }
}
