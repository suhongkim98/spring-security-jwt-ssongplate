package com.example.security.jwt.account.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RefreshTokenRequestDto(
        @NotNull
        String token
) {
}
