package com.example.security.jwt.member.controller;

import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.member.service.MemberService;
import com.example.security.jwt.member.dto.RequestMember;
import com.example.security.jwt.member.dto.ResponseMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // user 등록 API
    @PostMapping("/members")
    public ResponseEntity<CommonResponse> signup(
            @Valid @RequestBody RequestMember.Register registerDto
    ) {
        ResponseMember.Info userInfo = memberService.signup(registerDto);

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }
}