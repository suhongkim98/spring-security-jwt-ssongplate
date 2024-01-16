package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.RequestAccount;
import com.example.security.jwt.account.application.dto.ResponseAccount;

public interface AccountService {
    ResponseAccount.Token authenticate(String username, String password);

    ResponseAccount.Token refreshToken(String refreshToken);

    void invalidateRefreshTokenByUsername(String username);

    ResponseAccount.Information registerMember(RequestAccount.RegisterMember registerMemberDto);

    ResponseAccount.Information registerAdmin(RequestAccount.RegisterAdmin registerAdminDto);

    ResponseAccount.Information getAccountWithAuthorities(String username);

    ResponseAccount.Information getMyAccountWithAuthorities();
}
