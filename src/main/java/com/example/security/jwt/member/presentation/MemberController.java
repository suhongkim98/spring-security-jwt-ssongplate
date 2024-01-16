package com.example.security.jwt.member.presentation;

import com.example.security.jwt.global.dto.CommonResponse;
import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RequestMemberFacade;
import com.example.security.jwt.member.application.dto.ResponseMemberFacade;
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
    public ResponseEntity<CommonResponse> signup(
            @Valid @RequestBody RequestMemberFacade.Register registerDto
    ) {
        ResponseMemberFacade.Information userInfo = memberFacadeService.signup(registerDto);

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }
}