package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.*;

public interface AccountService {

    TokenResponseDto authenticate(TokenRequestDto tokenRequestDto);

    TokenResponseDto refreshToken(String refreshToken);

    void invalidateRefreshTokenByUsername(String username);

    void registerMember(RegisterMemberRequestDto requestDto);

    void registerAdmin(RegisterAdminRequestDto requestDto);

    AccountInfoResponseDto getAccountWithAuthorities(String username);
}
