package com.dcc.authservice.service;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;

public interface AuthUserService {

    LoginResultDto login(LoginRequestDto login);
}
