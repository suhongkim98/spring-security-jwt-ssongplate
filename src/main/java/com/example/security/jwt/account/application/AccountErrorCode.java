package com.example.security.jwt.account.application;

import com.example.security.jwt.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements BaseErrorCode {

    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "ACCOUNT_001", "리프레시 토큰이 유효하지 않습니다"),
    NOT_FOUND_ACCOUNT(HttpStatus.NOT_FOUND, "ACCOUNT_002", "계정을 찾을 수 없습니다"),
    ACCOUNT_DEACTIVATED(HttpStatus.FORBIDDEN, "ACCOUNT_003", "비활성화된 계정입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
