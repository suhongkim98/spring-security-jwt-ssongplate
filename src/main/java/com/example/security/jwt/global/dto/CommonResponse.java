package com.example.security.jwt.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse(
        String id,
        Date dateTime,
        Object response,
        Object error
) {
    @Builder
    public CommonResponse(
            String id,
            Date dateTime,
            Object response,
            Object error
    ) {
        this.id = UUID.randomUUID().toString();
        this.dateTime = new Date();
        this.response = response;
        this.error = error;
    }

    public static CommonResponse success(Object response) {
        return CommonResponse.builder()
                .response(response)
                .build();
    }

    public static CommonResponse fail(ErrorResponse errorResponse) {
        return CommonResponse.builder()
                .error(errorResponse)
                .build();
    }
}