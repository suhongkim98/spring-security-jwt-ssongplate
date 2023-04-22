package com.example.security.jwt.account.service;

import com.example.security.jwt.account.dto.ResponseAccount;

public interface AccountService
{
    ResponseAccount.Token authenticate(String username, String password);
    ResponseAccount.Token refreshToken(String refreshToken);
    void invalidateRefreshTokenByUsername(String username);
}
