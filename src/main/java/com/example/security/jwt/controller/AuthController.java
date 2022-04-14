package com.example.security.jwt.controller;

import com.example.security.jwt.controller.dto.CommonResponse;
import com.example.security.jwt.controller.dto.RequestAuth;
import com.example.security.jwt.controller.dto.ResponseAuth;
import com.example.security.jwt.security.JwtFilter;
import com.example.security.jwt.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    // 생성자주입
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate") // Account 인증 API
    public ResponseEntity<CommonResponse> authorize(@Valid @RequestBody RequestAuth.Login loginDto) {

        ResponseAuth.Token token = authService.authenticate(loginDto.getUsername(), loginDto.getPassword());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.getAccessToken());

        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(token)
                .build();

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }
}