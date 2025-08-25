package com.example.security.jwt.global.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;

// 리프레시 토큰 생성, 검증
// TokenProvider 기능을 확장
// 토큰 생성 시 가중치를 클레임에 넣는다.
// 토큰 검증 시 유저 가중치 > 리프레시 토큰 가중치라면 리프레시 토큰은 유효하지 않다.
@Slf4j
public final class RefreshTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long tokenValidityInSeconds;
    private static final String WEIGHT_KEY = "weight";

    public RefreshTokenProvider(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, long tokenValidityInSeconds) {
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
        this.tokenValidityInSeconds = tokenValidityInSeconds;
    }

    // 토큰 생성
    public Jwt createToken(Long accountId, Long tokenWeight) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(this.tokenValidityInSeconds))
                .subject(String.valueOf(accountId))
                .claim(WEIGHT_KEY, tokenWeight)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims));
    }

    public Jwt decodeFrom(String token) {
        return jwtDecoder.decode(token);
    }

    // 토큰에서 유저 네임을 꺼내 반환
    public Long getId(Jwt token) {
        return Long.parseLong(token.getSubject());
    }

    public long getTokenWeight(Jwt jwt) {
        // 토큰에서 가중치를 꺼내 반환한다.
        return Long.parseLong(String.valueOf(jwt.getClaims().get(WEIGHT_KEY)));
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            return true;
        } catch (JwtException jwtException) {
            log.info(jwtException.getMessage());
        }
        return false;
    }
}
