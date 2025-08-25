package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.*;

public interface AccountFacadeService {

    TokenResponseDto authenticate(TokenRequestDto tokenRequestDto);

    TokenResponseDto refreshToken(String refreshToken);

    void invalidateRefreshTokenById(Long accountId);

    AccountInfoResponseDto getAccountWithAuthorities(Long accountId);
}
