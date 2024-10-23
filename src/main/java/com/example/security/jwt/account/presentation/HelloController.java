package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.AccountService;
import com.example.security.jwt.account.application.dto.AccountInfoResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HelloController {

    private final AccountService accountService;

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
    public ResponseEntity<AccountInfoResponseDto> getMyUserInfo(Authentication authentication) {
        log.info(authentication.getName());
        log.info(authentication.getAuthorities().toString());
        return ResponseEntity.ok(accountService.getAccountWithAuthorities(authentication.getName()));
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')") // ADMIN 권한만 호출 가능
    @SecurityRequirement(name = "bearer-key")
    public ResponseEntity<AccountInfoResponseDto> getUserInfo(@PathVariable(name = "username") String username) {
        return ResponseEntity.ok(accountService.getAccountWithAuthorities(username));
    }
}
