package com.dcc.authservice.service.Impl;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.service.AuthUserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final JwtEncoder jwtEncoderAccessToken;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtEncoder jwtEncoderRefreshToken;

    @Override
    public LoginResultDto login(LoginRequestDto login) {
        // 1. Authenticate
        Authentication auth = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
        auth = daoAuthenticationProvider.authenticate(auth);

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Generate JWT token to response

        // 1. Define JwtClaimSet  (Payload)
        Instant now = Instant.now();
        JwtClaimsSet accessJwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .audience(List.of("REACT JS"))
                .claim("scope", scope)
                .build();

        JwtClaimsSet refreshJwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(5, ChronoUnit.DAYS))
                .audience(List.of("REACT JS"))
                .claim("scope", scope)
                .build();

        // 2. Generate Token
        String accessToken = jwtEncoderAccessToken
                .encode(JwtEncoderParameters.from(accessJwtClaimsSet))
                .getTokenValue();
        String refreshToken = jwtEncoderRefreshToken
                .encode(JwtEncoderParameters.from(refreshJwtClaimsSet))
                .getTokenValue();

        LoginResultDto resultDto = new LoginResultDto();
        resultDto.setAccessToken(accessToken);
        resultDto.setRefreshToken(refreshToken);
        resultDto.setTokenType("AccessToken");
        return resultDto;
    }
}
