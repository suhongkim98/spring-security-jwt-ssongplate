package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.dto.RefreshTokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.global.security.CustomJwtFilter;
import com.example.security.jwt.account.application.AccountService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/token") // Account 인증 API
    public ResponseEntity<CommonResponse> authorize(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto token = accountService.authenticate(tokenRequestDto.username(), tokenRequestDto.password());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/token") // 리프레시 토큰을 활용한 액세스 토큰 갱신
    public ResponseEntity<CommonResponse> refreshToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        TokenResponseDto token = accountService.refreshToken(requestDto.token());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    //리프레시토큰 만료 API
    //-> 해당 계정의 가중치를 1 올린다. 그럼 나중에 해당 리프레시 토큰으로 갱신 요청이 들어와도 받아들여지지 않음
    @DeleteMapping("/{username}/token")
    @PreAuthorize("hasAnyRole('ADMIN')") // ADMIN 권한만 호출 가능
    public ResponseEntity<Void> authorize(@PathVariable(name = "username") String username) {
        accountService.invalidateRefreshTokenByUsername(username);
        return ResponseEntity.noContent()
                .build();
    }
    // 계정 탈퇴 구현 시 계정을 삭제하지 않고 비활성화 시켜야한다. field activated
}