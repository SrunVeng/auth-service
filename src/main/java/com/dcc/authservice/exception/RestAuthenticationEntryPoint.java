package com.dcc.authservice.exception;

import com.dcc.sdkcentral.responseBuilder.ResponseMessage;
import com.dcc.sdkcentral.responseBuilder.ResponseMessageBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final ResponseMessageBuilder<Void> responseMessageBuilder;

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        ResponseMessage<Void> failed = responseMessageBuilder.failed();
        failed.setDevErrorCode(HttpStatus.UNAUTHORIZED.name());
        failed.setDevMessage(resolveMessage(authException));
        failed.setStatus(HttpStatus.UNAUTHORIZED);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getOutputStream(), failed);
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
