package com.example.security.jwt.controller;

import com.example.security.jwt.controller.dto.*;
import com.example.security.jwt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    // user 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(
            @Valid @RequestBody RequestUser.Register registerDto
    ) {
        ResponseUser.Info userInfo = userService.signup(registerDto);

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }
}