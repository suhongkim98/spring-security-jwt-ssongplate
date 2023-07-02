package com.example.security.jwt.account.dto;

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
