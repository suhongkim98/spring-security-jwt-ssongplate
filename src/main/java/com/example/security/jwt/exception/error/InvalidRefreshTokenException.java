package com.example.security.jwt.exception.error;

import com.example.security.jwt.exception.ErrorCode;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(){
        super(ErrorCode.INVALID_REFRESH_TOKEN.getMessage());
    }

}
