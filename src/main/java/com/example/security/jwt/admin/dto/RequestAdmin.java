package com.example.security.jwt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestAdmin {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Register {
        @NotNull
        @Size(min = 3, max = 50)
        private String username;

        @NotNull
        @Size(min = 5, max = 100)
        private String password;

        @NotNull
        @Size(min = 5, max = 100)
        private String nickname;
    }
}
