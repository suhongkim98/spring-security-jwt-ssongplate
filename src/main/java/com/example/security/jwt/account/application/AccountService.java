package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.AccountInfoResponseDto;
import com.example.security.jwt.account.application.dto.RegisterAdminRequestDto;
import com.example.security.jwt.account.application.dto.RegisterMemberRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;

public interface AccountService {

    TokenResponseDto authenticate(String username, String password);

    TokenResponseDto refreshToken(String refreshToken);

    void invalidateRefreshTokenByUsername(String username);

    void registerMember(RegisterMemberRequestDto requestDto);

    void registerAdmin(RegisterAdminRequestDto requestDto);

    AccountInfoResponseDto getAccountWithAuthorities(String username);

    AccountInfoResponseDto getMyAccountWithAuthorities();
}
