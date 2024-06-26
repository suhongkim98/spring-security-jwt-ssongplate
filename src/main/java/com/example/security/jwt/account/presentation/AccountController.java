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
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    @PostMapping("/token")
    public ResponseEntity<CommonResponse<TokenResponseDto>> authorize(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto token = accountService.authenticate(tokenRequestDto.username(), tokenRequestDto.password());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    @Override
    @PutMapping("/token")
    public ResponseEntity<CommonResponse<TokenResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto requestDto) {
        TokenResponseDto token = accountService.refreshToken(requestDto.token());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{username}/token")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> authorize(@PathVariable(name = "username") String username) {
        accountService.invalidateRefreshTokenByUsername(username);
        return ResponseEntity.noContent()
                .build();
    }
    // 계정 탈퇴 구현 시 계정을 삭제하지 않고 비활성화 시켜야한다. field activated
}