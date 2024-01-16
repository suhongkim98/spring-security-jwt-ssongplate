package com.example.security.jwt.admin.application;

import com.example.security.jwt.account.application.AccountService;
import com.example.security.jwt.account.application.dto.RequestAccount;
import com.example.security.jwt.account.application.dto.ResponseAccount;
import com.example.security.jwt.admin.application.dto.RequestAdminFacade;
import com.example.security.jwt.admin.application.dto.ResponseAdminFacade;
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
    public ResponseAdminFacade.Information signup(RequestAdminFacade.Register registerDto) {
        ResponseAccount.Information response = accountService.registerAdmin(RequestAccount.RegisterAdmin.builder()
                        .nickname(registerDto.nickname())
                        .password(registerDto.password())
                        .username(registerDto.username())
                .build());

        return ResponseAdminFacade.Information.builder()
                .authoritySet(response.authoritySet())
                .nickname(response.nickname())
                .tokenWeight(response.tokenWeight())
                .password(response.password())
                .username(response.username())
                .build();
    }
}
