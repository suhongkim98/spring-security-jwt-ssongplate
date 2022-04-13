package com.example.security.jwt.exception;

import com.example.security.jwt.controller.dto.CommonResponse;
import com.example.security.jwt.exception.error.DuplicateMemberException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Bean Validation에 실패했을 때, 에러메시지를 내보내기 위한 Exception Handler
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<CommonResponse> handleParamViolationException(BindException ex) {
        // 파라미터 validation에 걸렸을 경우

        ErrorResponse error = ErrorResponse.builder()
                .status(ErrorCode.REQUEST_PARAMETER_BIND_FAILED.getStatus().value())
                .message(ErrorCode.REQUEST_PARAMETER_BIND_FAILED.getMessage())
                .code(ErrorCode.REQUEST_PARAMETER_BIND_FAILED.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, ErrorCode.REQUEST_PARAMETER_BIND_FAILED.getStatus());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    protected ResponseEntity<CommonResponse> handleDuplicateMemberException(DuplicateMemberException ex) {

        ErrorResponse error = ErrorResponse.builder()
                .status(ErrorCode.DUPLICATE_MEMBER_EXCEPTION.getStatus().value())
                .message(ErrorCode.DUPLICATE_MEMBER_EXCEPTION.getMessage())
                .code(ErrorCode.DUPLICATE_MEMBER_EXCEPTION.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, ErrorCode.DUPLICATE_MEMBER_EXCEPTION.getStatus());
    }
}
