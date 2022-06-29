package com.example.security.jwt.global.config;

import com.example.security.jwt.global.security.RefreshTokenProvider;
import com.example.security.jwt.global.security.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String accessTokenSecret;
    @Value("${jwt.refresh-token-secret}")
    private String refreshTokenSecret;
    @Value("${jwt.access-token-validity-in-seconds}")
    private Long accessTokenValidityInSeconds;
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenValidityInSeconds;

    // 액세스 토큰 발급용, 리프레시 토큰 발급용은 각각 별도의 키와 유효기간을 갖는다.
    @Bean(name = "tokenProvider")
    public TokenProvider tokenProvider() {
        return new TokenProvider(accessTokenSecret, accessTokenValidityInSeconds);
    }

    // 리프레시 토큰은 별도의 키를 가지기 떄문에 리프레시 토큰으로는 API 호출 불가
    // 액세스 토큰 재발급 시 검증용
    @Bean(name = "refreshTokenProvider")
    public RefreshTokenProvider refreshTokenProvider() {
        return new RefreshTokenProvider(refreshTokenSecret, refreshTokenValidityInSeconds);
    }
}
