package com.dcc.authservice.service.Impl;

import com.dcc.authservice.AuthUser;
import com.dcc.authservice.service.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {


    private final PasswordEncoder passwordEncoder;


    public AuthUser findByUsername(String username) {
        // MVP only. Later replace this with database query.
        if (!"john".equalsIgnoreCase(username)) {
            return null;
        }

        return new AuthUser(
                "1001",
                "john",
                passwordEncoder.encode("123456"),
                List.of("USER"),
                List.of("account:read", "payment:create")
        );
    }

}
