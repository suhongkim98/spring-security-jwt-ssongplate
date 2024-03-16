package com.example.security.jwt.admin.application.dto;

import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Builder
public record RegisterAdminFacadeRequestDto(
        @NotNull
        @Size(min = 3, max = 50)
        String username,

        @NotNull
        @Size(min = 5, max = 100)
        String password,

        @NotNull
        @Size(min = 5, max = 100)
        String nickname
) {
}
