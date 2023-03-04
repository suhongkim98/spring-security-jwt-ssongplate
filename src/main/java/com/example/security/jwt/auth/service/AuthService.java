package com.example.security.jwt.auth.service;

import com.example.security.jwt.auth.dto.RequestAuth;
import com.example.security.jwt.auth.dto.ResponseAuth;

public interface AuthService {
    ResponseAuth.Token authenticate(RequestAuth.Authenticate authenticateDto);
    ResponseAuth.Token refreshToken(RequestAuth.RefreshToken refreshTokenDto);
    void invalidateRefreshTokenByUsername(String username);
}
