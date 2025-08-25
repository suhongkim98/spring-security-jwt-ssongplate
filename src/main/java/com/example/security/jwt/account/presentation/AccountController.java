package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.account.application.AccountFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController implements AccountApi {

    private final AccountFacadeService accountFacadeService;

    private static final String REFRESH_TOKEN_HEADER = "RT";

    @Override
    @PostMapping("/token")
    public ResponseEntity<CommonResponse<TokenResponseDto>> authorize(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        TokenResponseDto token = accountFacadeService.authenticate(tokenRequestDto);

        HttpHeaders httpHeaders = new HttpHeaders();
        setCookieRefreshToken(httpHeaders, token.refreshToken(), token.refreshTokenExpireAt());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    @Override
    @PutMapping("/token")
    public ResponseEntity<CommonResponse<TokenResponseDto>> refreshToken(@RequestHeader(name = REFRESH_TOKEN_HEADER) String refreshToken) {
        TokenResponseDto token = accountFacadeService.refreshToken(refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        setCookieRefreshToken(httpHeaders, token.refreshToken(), token.refreshTokenExpireAt());

        return new ResponseEntity<>(CommonResponse.success(token), httpHeaders, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{accountId}/token")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable(name = "accountId") Long accountId) {
        accountFacadeService.invalidateRefreshTokenById(accountId);
        return ResponseEntity.noContent()
                .build();
    }
    // 계정 탈퇴 구현 시 계정을 삭제하지 않고 비활성화 시켜야한다. field activated

    private void setCookieRefreshToken(HttpHeaders headers, String refreshToken, Instant expireAt) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_HEADER, refreshToken)
                .httpOnly(true)
                .secure(true) // only https 시에만
                .path("/")
                .maxAge(Duration.between(Instant.now(), expireAt))
                .sameSite("Strict")
                .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}