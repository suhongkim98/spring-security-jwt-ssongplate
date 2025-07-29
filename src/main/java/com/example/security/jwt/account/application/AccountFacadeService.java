package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.*;

public interface AccountFacadeService {

    TokenResponseDto authenticate(TokenRequestDto tokenRequestDto);

    TokenResponseDto refreshToken(String refreshToken);

    void invalidateRefreshTokenByUsername(String username);

    AccountInfoResponseDto getAccountWithAuthorities(String username);
}
