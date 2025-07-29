package com.example.security.jwt.member.application;

import com.example.security.jwt.global.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    CONFLICT_MEMBER_ACCOUNT(HttpStatus.CONFLICT, "MEMBER_001", "중복된 계정"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
