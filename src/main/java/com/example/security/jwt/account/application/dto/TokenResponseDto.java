package com.example.security.jwt.account.application.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TokenResponseDto(
        String accessToken,
        String refreshToken,
        Instant accessTokenExpireAt,
        Instant refreshTokenExpireAt
) {
}
