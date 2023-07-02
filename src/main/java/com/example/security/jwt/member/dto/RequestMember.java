package com.example.security.jwt.member.dto;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record RequestMember() {
    @Builder
    public record Register(
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
}
