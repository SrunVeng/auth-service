package com.dcc.authservice.service.Impl;


import com.dcc.authservice.AuthUser;
import com.dcc.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${auth.issuer}")
    private String issuer;

    @Value("${auth.access-token-expiration-minutes}")
    private long accessTokenExpirationMinutes;


    public String generateAccessToken(AuthUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenExpirationMinutes * 60);

        String scope = String.join(" ", user.scopes());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.id())
                .claim("username", user.username())
                .claim("roles", user.roles())
                .claim("scope", scope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public long getExpiresInSeconds() {
        return accessTokenExpirationMinutes * 60;
    }

}
