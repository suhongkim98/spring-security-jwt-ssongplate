package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.AccountFacadeService;
import com.example.security.jwt.account.application.dto.AccountInfoResponseDto;
import com.example.security.jwt.global.security.jwt.CustomAccountPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloController {

    private final AccountFacadeService accountFacadeService;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    // redirect test
    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    // 인가 테스트
    // Authorization: Bearer {AccessToken}
    @GetMapping("/user")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_MEMBER') or hasAuthority('SCOPE_ROLE_ADMIN')")
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<AccountInfoResponseDto> getMyUserInfo(@AuthenticationPrincipal CustomAccountPrincipal principal) {
        log.info(principal.getAccountId());
        return ResponseEntity.ok(accountFacadeService.getAccountWithAuthorities(Long.parseLong(principal.getAccountId())));
    }

    @GetMapping("/user/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')") // ADMIN 권한만 호출 가능
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<AccountInfoResponseDto> getUserInfo(@PathVariable(name = "accountId") Long accountId) {
        return ResponseEntity.ok(accountFacadeService.getAccountWithAuthorities(accountId));
    }
}
