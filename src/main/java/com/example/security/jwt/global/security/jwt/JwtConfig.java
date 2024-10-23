package com.example.security.jwt.global.security.jwt;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    // accessJwt 빈이 refresh보다 우선
    @Primary
    @Bean(name = "accessJwtDecoder")
    public JwtDecoder accessJwtDecoder(JwtProperties jwtProperties) {
        return NimbusJwtDecoder.withPublicKey(jwtProperties.getAccessPublicKey()).build();
    }

    @Primary
    @Bean(name = "accessJwtEncoder")
    public JwtEncoder accessJwtEncoder(JwtProperties jwtProperties) {
        JWK jwk = new RSAKey.Builder(jwtProperties.getAccessPublicKey())
                .privateKey(jwtProperties.getAccessPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean(name = "refreshJwtDecoder")
    public JwtDecoder refreshJwtDecoder(JwtProperties jwtProperties) {
        return NimbusJwtDecoder.withPublicKey(jwtProperties.getRefreshPublicKey()).build();
    }

    @Bean(name = "refreshJwtEncoder")
    public JwtEncoder refreshJwtEncoder(JwtProperties jwtProperties) {
        JWK jwk = new RSAKey.Builder(jwtProperties.getRefreshPublicKey())
                .privateKey(jwtProperties.getRefreshPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    // 액세스 토큰 발급용, 리프레시 토큰 발급용은 각각 별도의 키와 유효기간을 갖는다.
    @Bean(name = "accessTokenProvider")
    public AccessTokenProvider accessTokenProvider(JwtEncoder accessJwtEncoder,
                                                   JwtProperties jwtProperties) {
        return new AccessTokenProvider(accessJwtEncoder, jwtProperties.getAccessTokenValidityInSeconds());
    }

    // 리프레시 토큰은 별도의 키를 가지기 떄문에 리프레시 토큰으로는 API 호출 불가
    // 액세스 토큰 재발급 시 검증용
    @Bean(name = "refreshTokenProvider")
    public RefreshTokenProvider refreshTokenProvider(JwtEncoder refreshJwtEncoder,
                                                     JwtDecoder refreshJwtDecoder,
                                                     JwtProperties jwtProperties) {
        return new RefreshTokenProvider(refreshJwtEncoder, refreshJwtDecoder, jwtProperties.getRefreshTokenValidityInSeconds());
    }
}
