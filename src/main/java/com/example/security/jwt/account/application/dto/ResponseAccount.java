package com.example.security.jwt.account.application.dto;

import lombok.Builder;

public record ResponseAccount()
{
    @Builder
    public record Token(
            String accessToken,
            String refreshToken
    ) {
    }
}
