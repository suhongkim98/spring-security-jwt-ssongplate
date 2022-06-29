package com.example.security.jwt.auth.controller;

import com.example.security.jwt.user.dto.ResponseUser;
import com.example.security.jwt.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class HelloController {
    private final UserService userService;

    public HelloController(UserService userService) {
        this.userService = userService;
    }

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
    // @AuthenticationPrincipal를 통해 JwtFilter에서 토큰을 검증하며 등록한 시큐리티 유저 객체를 꺼내올 수 있다.
    // JwtFilter는 디비 조회를 하지 않기에 유저네임, 권한만 알 수 있음
    // Account 엔티티에 대한 정보를 알고 싶으면 당연 디비 조회를 별도로 해야함
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // USER, ADMIN 권한 둘 다 호출 허용
    public ResponseEntity<ResponseUser.Info> getMyUserInfo(@AuthenticationPrincipal User user) {
        System.out.println(user.getUsername() + " " + user.getAuthorities());
        return ResponseEntity.ok(userService.getMyUserWithAuthorities());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')") // ADMIN 권한만 호출 가능
    public ResponseEntity<ResponseUser.Info> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username));
    }
}
