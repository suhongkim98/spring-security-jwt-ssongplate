package com.example.security.jwt.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ResponseAuth {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Token {
        private String accessToken;
        private String refreshToken;
    }
}
