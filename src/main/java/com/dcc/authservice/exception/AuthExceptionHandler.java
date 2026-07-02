package com.dcc.authservice.exception;

import com.dcc.sdkcentral.responseBuilder.ResponseMessage;
import com.dcc.sdkcentral.responseBuilder.ResponseMessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final ResponseMessageBuilder<Void> responseMessageBuilder;

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseMessage<Void>> handleAuthenticationException(AuthenticationException ex) {
        ResponseMessage<Void> failed = responseMessageBuilder.failed();

        failed.setDevErrorCode(HttpStatus.UNAUTHORIZED.name());
        failed.setDevMessage(resolveMessage(ex));
        failed.setStatus(HttpStatus.UNAUTHORIZED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(failed);
    }

    private String resolveMessage(AuthenticationException ex) {
        if (ex instanceof LockedException) {
            return "User account is locked";
        }

        if (ex instanceof DisabledException) {
            return "User account is disabled";
        }

        if (ex instanceof AccountExpiredException) {
            return "User account has expired";
        }

        if (ex instanceof CredentialsExpiredException) {
            return "User credentials have expired";
        }

        if (ex instanceof BadCredentialsException) {
            return "Invalid username or password";
        }

        return "Authentication failed";
    }
}