package com.example.security.jwt.account.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterMemberRequestDto(
        @NotNull
        @Size(min = 3, max = 50)
        String username,

        @NotNull
        @Size(min = 5, max = 100)
        String password,

        @NotNull
        @Size(min = 5, max = 100)
        String nickname) {
}
