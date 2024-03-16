package com.example.security.jwt.member.application;

import com.example.security.jwt.account.application.AccountService;
import com.example.security.jwt.account.application.dto.RegisterMemberRequestDto;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFacadeServiceImpl implements MemberFacadeService {

    private final AccountService accountService;

    @Override
    public void signup(RegisterMemberFacadeRequestDto requestDto) {
        accountService.registerMember(RegisterMemberRequestDto.builder()
                        .nickname(requestDto.nickname())
                        .username(requestDto.username())
                        .password(requestDto.password())
                .build());
    }
}