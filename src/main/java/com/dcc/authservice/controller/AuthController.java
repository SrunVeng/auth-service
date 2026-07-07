package com.dcc.authservice.controller;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.dto.TokenValidateRequestDto;
import com.dcc.authservice.dto.TokenValidateResultDto;
import com.dcc.authservice.mapper.AuthMapper;
import com.dcc.authservice.service.AuthUserService;
import com.dcc.authservice.vo.LoginRequestVo;
import com.dcc.authservice.vo.LoginResponseVo;
import com.dcc.authservice.vo.TokenValidateRequestVo;
import com.dcc.authservice.vo.TokenValidateResponseVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUserService authUserService;
    private final AuthMapper authMapper;

    @PostMapping("/login")
    public LoginResponseVo login(@Valid @RequestBody LoginRequestVo requestVo) {
        LoginRequestDto requestDto = authMapper.toRequestDto(requestVo);
        LoginResultDto login = authUserService.login(requestDto);
        return authMapper.toResponseVo(login);
    }

    @PostMapping("/validate")
    public TokenValidateResponseVo login(@Valid @RequestBody TokenValidateRequestVo requestVo) {
        TokenValidateRequestDto requestDto = authMapper.toRequestDto(requestVo);
        TokenValidateResultDto login = authUserService.validate(requestDto);
        return authMapper.toResponseVo(login);
    }
}
