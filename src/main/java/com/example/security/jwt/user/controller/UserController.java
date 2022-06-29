package com.example.security.jwt.user.controller;

import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.user.service.UserService;
import com.example.security.jwt.user.dto.RequestUser;
import com.example.security.jwt.user.dto.ResponseUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // user 등록 API
    @PostMapping("/user/signup")
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