package com.example.security.jwt.global.exception;

import lombok.Getter;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class ApplicationException extends ResponseStatusException {

    private final BaseErrorCode errorCode;

    public ApplicationException(BaseErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApplicationException(BaseErrorCode errorCode, String message) {
        super(errorCode.getHttpStatus(), message);
        this.errorCode = errorCode;
    }
}
