package com.dcc.authservice.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TokenValidateRequestVo {

    @NotBlank(message = "Token is required")
    private String token;
}
