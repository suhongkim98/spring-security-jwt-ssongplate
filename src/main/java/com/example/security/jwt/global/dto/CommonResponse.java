package com.example.security.jwt.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Date;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(
        String id,
        Date dateTime,
        T response,
        ErrorResponse error
) {
    @Builder
    public CommonResponse(
            String id,
            Date dateTime,
            T response,
            ErrorResponse error
    ) {
        this.id = UUID.randomUUID().toString();
        this.dateTime = new Date();
        this.response = response;
        this.error = error;
    }

    public static <T> CommonResponse<T> success(T response) {
        return CommonResponse.<T>builder()
                .response(response)
                .build();
    }

    public static CommonResponse<ErrorResponse> fail(ErrorResponse errorResponse) {
        return CommonResponse.<ErrorResponse>builder()
                .error(errorResponse)
                .build();
    }
}