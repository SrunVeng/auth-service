package com.dcc.authservice.service;

import com.dcc.authservice.AuthUser;

public interface JwtService {


    String generateAccessToken(AuthUser user);


    long getExpiresInSeconds();
}
