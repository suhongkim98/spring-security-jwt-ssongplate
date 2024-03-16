package com.example.security.jwt.member.presentation;

import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberFacadeService memberFacadeService;

    public MemberController(MemberFacadeService memberFacadeService) {
        this.memberFacadeService = memberFacadeService;
    }

    // user 등록 API
    @PostMapping("/members")
    public ResponseEntity<Void> signup(
            @Valid @RequestBody RegisterMemberFacadeRequestDto requestDto
    ) {
        memberFacadeService.signup(requestDto);

        return ResponseEntity
                .noContent()
                .build();
    }
}