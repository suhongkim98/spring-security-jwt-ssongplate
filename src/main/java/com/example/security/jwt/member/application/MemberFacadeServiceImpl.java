package com.example.security.jwt.member.application;

import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.account.domain.exception.AccountConflictDomainException;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFacadeServiceImpl implements MemberFacadeService {

    private final AccountDomainService accountDomainService;

    @Override
    public void signup(RegisterMemberFacadeRequestDto requestDto) {
        try {
            accountDomainService.createUserAccount(requestDto.username(), requestDto.password(), requestDto.nickname());
        } catch (AccountConflictDomainException accountConflictDomainException) {
            throw new ApplicationException(MemberErrorCode.CONFLICT_MEMBER_ACCOUNT);
        }
    }
}