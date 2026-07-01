package com.dcc.authservice;

import java.util.List;

public record AuthUser(
        String id,
        String username,
        String password,
        List<String> roles,
        List<String> scopes
) {
}