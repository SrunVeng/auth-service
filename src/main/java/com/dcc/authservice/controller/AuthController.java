package com.dcc.authservice.controller;

import com.dcc.authservice.AuthUser;
import com.dcc.authservice.dto.LoginRequest;
import com.dcc.authservice.dto.LoginResponse;
import com.dcc.authservice.service.AuthUserService;
import com.dcc.authservice.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthUser user = authUserService.findByUsername(request.username());

        if (user == null || !passwordEncoder.matches(request.password(), user.password())) {
            throw new InvalidCredentialException();
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getExpiresInSeconds()
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class InvalidCredentialException extends RuntimeException {
    }
}