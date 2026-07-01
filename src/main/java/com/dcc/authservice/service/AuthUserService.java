package com.dcc.authservice.service;

import com.dcc.authservice.AuthUser;

public interface AuthUserService {

    AuthUser findByUsername(String username);

}
