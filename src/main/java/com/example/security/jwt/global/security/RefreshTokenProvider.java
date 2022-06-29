package com.example.security.jwt.global.security;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.stream.Collectors;

// 리프레시 토큰 생성, 검증
// TokenProvider 기능을 확장
// 토큰 생성 시 가중치를 클레임에 넣는다.
// 토큰 검증 시 유저 가중치 > 리프레시 토큰 가중치라면 리프레시 토큰은 유효하지 않다.

public class RefreshTokenProvider extends TokenProvider {
    private static final String WEIGHT_KEY = "token-weight";

    public RefreshTokenProvider(String secret, long tokenValidityInSeconds) {
        super(secret, tokenValidityInSeconds);
    }

    // 토큰 생성
    public String createToken(Authentication authentication, Long tokenWeight) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + super.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim(WEIGHT_KEY, tokenWeight)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public long getTokenWeight(String token) {
        // 토큰에서 가중치를 꺼내 반환한다.
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return Long.valueOf(String.valueOf(claims.get(WEIGHT_KEY)));
    }
}
