package com.example.security.jwt.auth.service;

import com.example.security.jwt.auth.dto.ResponseAuth;

public interface AuthService {
    ResponseAuth.Token authenticate(String username, String password);
    ResponseAuth.Token refreshToken(String refreshToken);
    void invalidateRefreshTokenByUsername(String username);
}
