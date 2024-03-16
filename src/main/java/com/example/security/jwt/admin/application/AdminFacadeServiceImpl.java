package com.example.security.jwt.admin.application;

import com.example.security.jwt.account.application.AccountService;
import com.example.security.jwt.account.application.dto.RegisterAdminRequestDto;
import com.example.security.jwt.admin.application.dto.RegisterAdminFacadeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AdminFacadeServiceImpl implements AdminFacadeService {

    private final AccountService accountService;

    // 회원가입 메서드
    @Override
    @Transactional
    public void signup(RegisterAdminFacadeRequestDto requestDto) {
        accountService.registerAdmin(RegisterAdminRequestDto.builder()
                        .nickname(requestDto.nickname())
                        .password(requestDto.password())
                        .username(requestDto.username())
                .build());
    }
}
