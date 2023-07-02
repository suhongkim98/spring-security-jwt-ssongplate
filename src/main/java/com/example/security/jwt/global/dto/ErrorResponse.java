package com.example.security.jwt.global.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ErrorResponse(
        String code,
        String message,
        int status
) {

}
