package com.example.security.jwt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

    // 인증, 인가 실패는 security.hander 쪽에 있음
    CONFLICT(HttpStatus.CONFLICT, "COMMON_001", "리소스 중복"),
    REQUEST_PARAMETER_BIND_FAILED(HttpStatus.BAD_REQUEST, "COMMON_002", "PARAMETER_BIND_FAILED"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_003", "BAD REQUEST"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "INTERNAL_SERVER_ERROR"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
