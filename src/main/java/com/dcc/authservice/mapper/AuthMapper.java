package com.dcc.authservice.mapper;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.vo.LoginRequestVo;
import com.dcc.authservice.vo.LoginResponseVo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    LoginRequestDto toRequestDto(LoginRequestVo loginRequestVo);

    LoginResponseVo toResponseVo(LoginResultDto loginResultDto);
}
