package com.example.security.jwt.member.facacde;

import com.example.security.jwt.account.application.AccountService;
import com.example.security.jwt.account.application.dto.RequestAccount;
import com.example.security.jwt.account.application.dto.ResponseAccount;
import com.example.security.jwt.member.facacde.dto.RequestMemberFacade;
import com.example.security.jwt.member.facacde.dto.ResponseMemberFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFacadeImpl implements MemberFacade {

    private final AccountService accountService;

    @Override
    public ResponseMemberFacade.Information signup(RequestMemberFacade.Register registerDto) {
        ResponseAccount.Information response = accountService.registerMember(RequestAccount.RegisterMember.builder()
                        .nickname(registerDto.nickname())
                        .username(registerDto.username())
                        .password(registerDto.password())
                .build());

        return ResponseMemberFacade.Information.builder()
                .authoritySet(response.authoritySet())
                .nickname(response.nickname())
                .tokenWeight(response.tokenWeight())
                .password(response.password())
                .username(response.username())
                .build();
    }
}