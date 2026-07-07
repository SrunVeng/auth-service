package com.dcc.authservice.service.Impl;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.dto.TokenValidateRequestDto;
import com.dcc.authservice.dto.TokenValidateResultDto;
import com.dcc.authservice.service.AuthUserService;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    @Value("${auth.secret-key}")
    private String secretKey;

    @Override
    public LoginResultDto login(LoginRequestDto login) {
        // 1. Authenticate
        Authentication auth = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
        auth = daoAuthenticationProvider.authenticate(auth);

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Generate JWT token to response

        Instant now = Instant.now();
        SecretKey signInKey = getSignInKey();
        String accessToken = Jwts.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
                .audience().add("REACT JS").and()
                .claim("scope", scope)
                .signWith(signInKey)
                .compact();
        String refreshToken = Jwts.builder()
                .id(auth.getName())
                .subject(auth.getName())
                .issuer(auth.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(5, ChronoUnit.DAYS)))
                .audience().add("REACT JS").and()
                .claim("scope", scope)
                .signWith(signInKey)
                .compact();

        LoginResultDto resultDto = new LoginResultDto();
        resultDto.setAccessToken(accessToken);
        resultDto.setRefreshToken(refreshToken);
        resultDto.setTokenType("AccessToken");
        return resultDto;
    }

    @Override
    public TokenValidateResultDto validate(TokenValidateRequestDto requestDto) {
        TokenValidateResultDto resultDto = new TokenValidateResultDto();
        try {
            resultDto.setUserName(extractUsername(requestDto.getToken()));
            resultDto.setValid(true);
        } catch (Exception ex) {
            resultDto.setValid(false);
            resultDto.setUserName(null);
        }
        return resultDto;
    }

    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
