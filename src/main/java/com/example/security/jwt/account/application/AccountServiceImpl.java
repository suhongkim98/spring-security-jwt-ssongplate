package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.*;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.AccountErrorCode;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.account.domain.AccountRepository;
import com.example.security.jwt.global.exception.CommonErrorCode;
import com.example.security.jwt.global.security.jwt.RefreshTokenProvider;
import com.example.security.jwt.global.security.jwt.AccessTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // username 과 패스워드로 사용자를 인증하여 액세스토큰과 리프레시 토큰을 반환한다.
    @Override
    public TokenResponseDto authenticate(TokenRequestDto tokenRequestDto) {
        Account account = accountRepository.findOneWithAuthoritiesByUsername(tokenRequestDto.username())
                .orElseThrow(() -> new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT));

        if (!passwordEncoder.matches(tokenRequestDto.password(), account.getPassword())) {
            throw new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT);
        }
        if(!account.isActivated()) {
            throw new ApplicationException(AccountErrorCode.ACCOUNT_DEACTIVATED);
        }

        Set<String> authorities = account.getAuthorities().stream()
                .map(Authority::getAuthorityName).collect(Collectors.toSet());
        String accessToken = accessTokenProvider.createToken(account.getUsername(), authorities);
        String refreshToken = refreshTokenProvider.createToken(account.getUsername(), account.getTokenWeight());

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponseDto refreshToken(String refreshToken) {
        // 먼저 리프레시 토큰을 검증한다.
        if(!refreshTokenProvider.validateToken(refreshToken)) {
            throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰에서 사용자를 꺼낸다.
        String username = refreshTokenProvider.getUsername(refreshToken);
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(()-> new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT));

        // 사용자 디비 값에 있는 것과 가중치 비교, 디비 가중치가 더 크다면 유효하지 않음
        if(account.getTokenWeight() > refreshTokenProvider.getTokenWeight(refreshToken)) {
            throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 액세스 토큰 생성
        Set<String> authorities = account.getAuthorities().stream()
                .map(Authority::getAuthorityName).collect(Collectors.toSet());
        String accessToken = accessTokenProvider.createToken(account.getUsername(), authorities);

        // 기존 리프레시 토큰과 새로 만든 액세스 토큰을 반환한다.
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Account 가중치를 1 올림으로써 해당 username 리프레시토큰 무효화
    @Override
    @Transactional
    public void invalidateRefreshTokenByUsername(String username) {
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT));
        account.increaseTokenWeight();
    }

    @Override
    @Transactional
    public void registerMember(RegisterMemberRequestDto requestDto) {
        Optional<Account> accountOptional = accountRepository.findOneWithAuthoritiesByUsername(requestDto.username());

        if (accountOptional.isPresent()) {
            throw new ApplicationException(CommonErrorCode.CONFLICT, "이미 가입되어있는 유저");
        }

        // 이 유저는 권한이 MEMBER
        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_MEMBER")
                .build();

        Account user = Account.builder()
                .username(requestDto.username())
                .password(passwordEncoder.encode(requestDto.password()))
                .nickname(requestDto.nickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        accountRepository.save(user);
    }

    @Override
    @Transactional
    public void registerAdmin(RegisterAdminRequestDto requestDto) {
        Optional<Account> accountOptional = accountRepository.findOneWithAuthoritiesByUsername(
                requestDto.username());

        if (accountOptional.isPresent()) {
            throw new ApplicationException(CommonErrorCode.CONFLICT, "이미 가입되어있는 유저");
        }

        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_ADMIN")
                .build();

        Account user = Account.builder()
                .username(requestDto.username())
                .password(passwordEncoder.encode(requestDto.password()))
                .nickname(requestDto.nickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        accountRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountInfoResponseDto getAccountWithAuthorities(String username) {
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT));
        return AccountInfoResponseDto.of(account);
    }
}
