package com.example.security.jwt.global.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private RSAPublicKey accessPublicKey;
    private RSAPrivateKey accessPrivateKey;
    private RSAPublicKey refreshPublicKey;
    private RSAPrivateKey refreshPrivateKey;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
