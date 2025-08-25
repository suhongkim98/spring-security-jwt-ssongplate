package com.example.security.jwt.global.security.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Set;

// 토큰 생성, 검증
public final class AccessTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final long tokenValidityInSeconds;
    private static final String AUTHORITIES_KEY = "scp"; // spring security 기본값 (scope)

    public AccessTokenProvider(JwtEncoder jwtEncoder, long tokenValidityInSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    // 토큰 생성
    public Jwt createToken(Long accountId, Set<String> authorities) {
        String strAuthorities = String.join(" ", authorities);

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(this.tokenValidityInSeconds))
                .subject(String.valueOf(accountId))
                .claim(AUTHORITIES_KEY, strAuthorities)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }
}