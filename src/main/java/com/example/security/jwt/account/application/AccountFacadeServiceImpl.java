package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.*;
import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.global.exception.DomainException;
import com.example.security.jwt.global.security.jwt.RefreshTokenProvider;
import com.example.security.jwt.global.security.jwt.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountFacadeServiceImpl implements AccountFacadeService {

    private final AccountDomainService accountDomainService;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;

    // username 과 패스워드로 사용자를 인증하여 액세스토큰과 리프레시 토큰을 반환한다.
    @Override
    public TokenResponseDto authenticate(TokenRequestDto tokenRequestDto) {
        Account account;
        try {
            account = accountDomainService.getOneByUsername(tokenRequestDto.username());
        } catch (DomainException e) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }
        if (!accountDomainService.isMatchPassword(tokenRequestDto.password(), account.getPassword())) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }
        if (!account.isActivated()) {
            throw new ApplicationException(AccountErrorCode.ACCOUNT_DEACTIVATED);
        }

        Set<String> authorities = account.getAuthorities().stream()
                .map(Authority::getAuthorityName).collect(Collectors.toSet());
        Jwt accessToken = accessTokenProvider.createToken(account.getId(), authorities);
        Jwt refreshToken = refreshTokenProvider.createToken(account.getId(), account.getTokenWeight());

        return TokenResponseDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(refreshToken.getTokenValue())
                .accessTokenExpireAt(accessToken.getExpiresAt())
                .refreshTokenExpireAt(refreshToken.getExpiresAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponseDto refreshToken(String refreshToken) {
        // 먼저 리프레시 토큰을 검증한다.
        if (!refreshTokenProvider.validateToken(refreshToken)) {
            throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);
        }

        Jwt decodedToken = refreshTokenProvider.decodeFrom(refreshToken);

        // 리프레시 토큰에서 사용자를 꺼낸다.
        Long accountId  = refreshTokenProvider.getId(decodedToken);
        Account account;
        try {
            account = accountDomainService.getOneById(accountId);
        } catch (DomainException e) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }

        // 사용자 디비 값에 있는 것과 가중치 비교, 디비 가중치가 더 크다면 유효하지 않음
        if (account.getTokenWeight() > refreshTokenProvider.getTokenWeight(decodedToken)) {
            throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 액세스 토큰 생성
        Set<String> authorities = account.getAuthorities().stream()
                .map(Authority::getAuthorityName).collect(Collectors.toSet());
        Jwt accessToken = accessTokenProvider.createToken(account.getId(), authorities);

        // 기존 리프레시 토큰과 새로 만든 액세스 토큰을 반환한다.
        return TokenResponseDto.builder()
                .accessToken(accessToken.getTokenValue())
                .refreshToken(decodedToken.getTokenValue())
                .accessTokenExpireAt(accessToken.getExpiresAt())
                .refreshTokenExpireAt(decodedToken.getExpiresAt())
                .build();
    }

    // Account 가중치를 1 올림으로써 해당 username 리프레시토큰 무효화
    @Override
    @Transactional
    public void invalidateRefreshTokenById(Long accountId) {
        Account account;
        try {
            account = accountDomainService.getOneById(accountId);
        } catch (DomainException e) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }
        account.increaseTokenWeight();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountInfoResponseDto getAccountWithAuthorities(Long accountId) {
        Account account;
        try {
            account = accountDomainService.getOneById(accountId);
        } catch (DomainException e) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }
        return AccountInfoResponseDto.of(account);
    }
}
