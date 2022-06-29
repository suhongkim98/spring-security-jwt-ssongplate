package com.example.security.jwt.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestAuth {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        @NotNull
        @Size(min = 3, max = 50)
        private String username;

        @NotNull
        @Size(min = 5, max = 100)
        private String password;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Refresh {
        @NotNull
        private String token;

    }
}
