package com.example.security.jwt.global.dto;

import lombok.Builder;

import java.util.Date;
import java.util.UUID;

public record CommonResponse(
        String id,
        Date dateTime,
        Boolean success,
        Object response,
        Object error
) {
    @Builder
    public CommonResponse(
            String id,
            Date dateTime,
            Boolean success,
            Object response,
            Object error
    ) {
        this.id = UUID.randomUUID().toString();
        this.dateTime = new Date();
        this.success = success;
        this.response = response;
        this.error = error;
    }
}