package com.dcc.authservice.service;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.dto.TokenValidateRequestDto;
import com.dcc.authservice.dto.TokenValidateResultDto;

public interface AuthUserService {

    LoginResultDto login(LoginRequestDto login);

    TokenValidateResultDto validate(TokenValidateRequestDto requestDto);
}
