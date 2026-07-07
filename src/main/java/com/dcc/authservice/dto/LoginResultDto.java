package com.dcc.authservice.dto;

import lombok.Data;

@Data
public class LoginResultDto {

    private String accessToken;
    private String tokenType;
}
