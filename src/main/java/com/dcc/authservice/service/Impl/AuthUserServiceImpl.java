package com.dcc.authservice.service.Impl;

import com.dcc.authservice.dto.LoginRequestDto;
import com.dcc.authservice.dto.LoginResultDto;
import com.dcc.authservice.dto.TokenValidateRequestDto;
import com.dcc.authservice.dto.TokenValidateResultDto;
import com.dcc.authservice.service.AuthUserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtEncoder accessTokenEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${auth.issuer:http://localhost:9000}")
    private String issuer;

    @Value("${auth.access-token-expiration-minutes:60}")
    private long accessTokenExpirationMinutes;

    public AuthUserServiceImpl(
            DaoAuthenticationProvider daoAuthenticationProvider,
            @Qualifier("jwtEncoderAccessToken") JwtEncoder accessTokenEncoder,
            JwtDecoder jwtDecoder) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.accessTokenEncoder = accessTokenEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public LoginResultDto login(LoginRequestDto login) {
        Authentication auth = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
        auth = daoAuthenticationProvider.authenticate(auth);

        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant now = Instant.now();
        String accessToken = buildToken(
                accessTokenEncoder,
                auth.getName(),
                scope,
                ACCESS_TOKEN_TYPE,
                now,
                now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES));

        LoginResultDto resultDto = new LoginResultDto();
        resultDto.setAccessToken(accessToken);
        resultDto.setTokenType("Bearer");
        return resultDto;
    }

    @Override
    public TokenValidateResultDto validate(TokenValidateRequestDto requestDto) {
        TokenValidateResultDto resultDto = new TokenValidateResultDto();
        try {
            String token = normalizeBearerToken(requestDto.getToken());
            Jwt jwt = jwtDecoder.decode(token);

            String userName = jwt.getSubject();
            String tokenType = jwt.getClaimAsString(TOKEN_TYPE_CLAIM);
            if (isBlank(userName) || !ACCESS_TOKEN_TYPE.equals(tokenType)) {
                return invalid(resultDto);
            }

            resultDto.setValid(true);
            resultDto.setUserName(userName);
        } catch (Exception ex) {
            return invalid(resultDto);
        }
        return resultDto;
    }


    private String buildToken(
            JwtEncoder encoder,
            String userName,
            String scope,
            String tokenType,
            Instant issuedAt,
            Instant expiresAt) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .issuer(issuer)
                .subject(userName)
                .audience(List.of("orderservice"))
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("scope", scope)
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private TokenValidateResultDto invalid(TokenValidateResultDto resultDto) {
        resultDto.setValid(false);
        resultDto.setUserName(null);
        return resultDto;
    }

    private String normalizeBearerToken(String token) {
        if (token == null) {
            return "";
        }
        String trimmed = token.trim();
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            return trimmed.substring("Bearer ".length()).trim();
        }
        return trimmed;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
