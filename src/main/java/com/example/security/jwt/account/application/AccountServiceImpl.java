package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.AccountInfoResponseDto;
import com.example.security.jwt.account.application.dto.RegisterAdminRequestDto;
import com.example.security.jwt.account.application.dto.RegisterMemberRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.account.domain.entity.AccountAdapter;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.AccountErrorCode;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.account.domain.AccountRepository;
import com.example.security.jwt.global.exception.CommonErrorCode;
import com.example.security.jwt.global.security.RefreshTokenProvider;
import com.example.security.jwt.global.security.AccessTokenProvider;
import com.example.security.jwt.global.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    // username 과 패스워드로 사용자를 인증하여 액세스토큰과 리프레시 토큰을 반환한다.
    @Override
    public TokenResponseDto authenticate(String username, String password) {
        // 받아온 유저네임과 패스워드를 이용해 UsernamePasswordAuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // authenticationToken 객체를 통해 Authentication 객체 생성
        // 이 과정에서 CustomUserDetailsService 에서 우리가 재정의한 loadUserByUsername 메서드 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기준으로 jwt access 토큰 생성
        String accessToken = accessTokenProvider.createToken(authentication);

        // 위에서 loadUserByUsername를 호출하였으므로 AccountAdapter가 시큐리티 컨텍스트에 저장되어 Account 엔티티 정보를 우리는 알 수 있음
        // 유저 정보에서 중치를 꺼내 리프레시 토큰 가중치에 할당, 나중에 액세스토큰 재발급 시도 시 유저정보 가중치 > 리프레시 토큰이라면 실패
        Long tokenWeight = ((AccountAdapter)authentication.getPrincipal()).getAccount().getTokenWeight();
        String refreshToken = refreshTokenProvider.createToken(authentication, tokenWeight);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponseDto refreshToken(String refreshToken) {
        // 먼저 리프레시 토큰을 검증한다.
        if(!refreshTokenProvider.validateToken(refreshToken)) throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);

        // 리프레시 토큰 값을 이용해 사용자를 꺼낸다.
        // refreshTokenProvider과 TokenProvider는 다른 서명키를 가지고 있기에 refreshTokenProvider를 써야함
        Authentication authentication = refreshTokenProvider.getAuthentication(refreshToken);
        Account account = accountRepository.findOneWithAuthoritiesByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException(authentication.getName() + "을 찾을 수 없습니다"));
        // 사용자 디비 값에 있는 것과 가중치 비교, 디비 가중치가 더 크다면 유효하지 않음
        if(account.getTokenWeight() > refreshTokenProvider.getTokenWeight(refreshToken)) throw new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN);

        // 리프레시 토큰에 담긴 값을 그대로 액세스 토큰 생성에 활용한다.
        String accessToken = accessTokenProvider.createToken(authentication);
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
                .orElseThrow(() -> new UsernameNotFoundException(username + "-> 찾을 수 없습니다"));
        account.increaseTokenWeight(); // 더티체킹에 의해 엔티티 변화 반영
    }

    @Override
    @Transactional
    public void registerMember(RegisterMemberRequestDto requestDto) {
        Optional<Account> accountOptional = accountRepository.findOneWithAuthoritiesByUsername(
                requestDto.username());

        if (accountOptional.isPresent()) {
            throw new ApplicationException(CommonErrorCode.CONFLICT, "이미 가입되어있는 유저");
        }

        // 이 유저는 권한이 ROLE_MEMBER
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
                .orElseThrow(() -> new UsernameNotFoundException(username + "-> 찾을 수 없습니다"));
        return AccountInfoResponseDto.of(account);
    }

    // 현재 시큐리티 컨텍스트에 저장된 username에 해당하는 정보를 가져온다.
    @Override
    @Transactional(readOnly = true)
    public AccountInfoResponseDto getMyAccountWithAuthorities() {
        Account account = securityUtil.getCurrentUsername()
                .flatMap(accountRepository::findOneWithAuthoritiesByUsername)
                .orElseThrow(() -> new UsernameNotFoundException("security context로부터 찾을 수 없습니다"));
        return AccountInfoResponseDto.of(account);
    }
}
