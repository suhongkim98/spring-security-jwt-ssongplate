package com.example.security.jwt.auth.controller;

import com.example.security.jwt.auth.dto.ResponseAuth;
import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.auth.dto.RequestAuth;
import com.example.security.jwt.global.security.JwtFilter;
import com.example.security.jwt.auth.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
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

    @PostMapping("/token/refresh") // 리프레시 토큰을 활용한 액세스 토큰 갱신
    public ResponseEntity<CommonResponse> refreshToken(@Valid @RequestBody RequestAuth.Refresh refreshDto) {

        ResponseAuth.Token token = authService.refreshToken(refreshDto.getToken());

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

    //리프레시토큰 만료 API
    //-> 해당 계정의 가중치를 1 올린다. 그럼 나중에 해당 리프레시 토큰으로 갱신 요청이 들어와도 받아들여지지 않음
    @DeleteMapping("/token/refresh/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')") // ADMIN 권한만 호출 가능
    public ResponseEntity<CommonResponse> authorize(@PathVariable String username) {
        authService.invalidateRefreshTokenByUsername(username);
        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // 계정 탈퇴 구현 시 계정을 삭제하지 않고 비활성화 시켜야한다. field activated
}