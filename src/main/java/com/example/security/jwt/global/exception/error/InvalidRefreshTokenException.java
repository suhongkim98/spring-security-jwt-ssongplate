package com.example.security.jwt.global.exception.error;

import com.example.security.jwt.global.exception.ErrorCode;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(){
        super(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

}
