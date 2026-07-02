package com.dcc.authservice.vo;

import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LoginResponseVo {

    private String accessToken;
    private String refreshToken;
    private String TokenType;
}
