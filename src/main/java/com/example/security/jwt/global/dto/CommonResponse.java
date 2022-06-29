package com.example.security.jwt.global.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.UUID;

@Getter
@Builder //.build로 객체 생성하게 해줌
public class CommonResponse {
    @Builder.Default // Builder default 지정
    private String id = UUID.randomUUID().toString(); // uuid
    @Builder.Default
    private Date dateTime = new Date(); // date
    private Boolean success;
    private Object response;
    private Object error;
}