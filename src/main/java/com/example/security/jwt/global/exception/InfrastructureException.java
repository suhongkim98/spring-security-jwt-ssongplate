package com.example.security.jwt.global.exception;

import lombok.Getter;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class InfrastructureException extends ResponseStatusException {

    private final CommonErrorCode errorCode;

    public InfrastructureException(CommonErrorCode errorCode) { // infrastructure 레이어는 CommonErrorCode만 받을 수 있음
        super(errorCode.getHttpStatus(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public InfrastructureException(CommonErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), message);
        this.errorCode = errorCode;
    }
}
