package com.dcc.authservice.mapper;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.dto.TokenValidateRequestDto;
import com.dcc.authservice.dto.TokenValidateResultDto;
import com.dcc.authservice.vo.LoginRequestVo;
import com.dcc.authservice.vo.LoginResponseVo;
import com.dcc.authservice.vo.TokenValidateRequestVo;
import com.dcc.authservice.vo.TokenValidateResponseVo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    LoginRequestDto toRequestDto(LoginRequestVo requestVo);

    LoginResponseVo toResponseVo(LoginResultDto resultDto);

    TokenValidateRequestDto toRequestDto(TokenValidateRequestVo requestVo);

    TokenValidateResponseVo toResponseVo(TokenValidateResultDto resultDto);


}
