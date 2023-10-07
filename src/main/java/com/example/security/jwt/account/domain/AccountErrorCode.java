package com.example.security.jwt.account.domain;

import com.example.security.jwt.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements BaseErrorCode {
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "ACCOUNT_001", "리프레시 토큰이 유효하지 않습니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
