package com.example.security.jwt.account.application;

import com.example.security.jwt.account.application.dto.AccountInfoResponseDto;
import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.account.domain.exception.AccountNotFoundDomainException;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.global.security.jwt.AccessTokenProvider;
import com.example.security.jwt.global.security.jwt.RefreshTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AccountFacadeServiceTest {

    @Mock
    private AccountDomainService accountDomainService;

    @Mock
    private AccessTokenProvider accessTokenProvider;

    @Mock
    private RefreshTokenProvider refreshTokenProvider;

    @InjectMocks
    private AccountFacadeServiceImpl accountFacadeService;

    // ==================== authenticate ====================

    @Test
    @DisplayName("인증 - 유효한 자격증명이면 토큰을 반환한다")
    void authenticate_whenValidCredentials_returnsTokenResponse() {
        // given
        TokenRequestDto request = TokenRequestDto.builder()
                .username("testuser")
                .password("testpassword")
                .build();
        Account account = activatedMemberAccount(1L, "testuser", "encodedPw");
        Jwt accessJwt = buildJwt("access-token", 600);
        Jwt refreshJwt = buildJwt("refresh-token", 86400);

        given(accountDomainService.getOneByUsername("testuser")).willReturn(account);
        given(accountDomainService.isMatchPassword("testpassword", "encodedPw")).willReturn(true);
        given(accessTokenProvider.createToken(eq(1L), anySet())).willReturn(accessJwt);
        given(refreshTokenProvider.createToken(eq(1L), eq(1L))).willReturn(refreshJwt);

        // when
        TokenResponseDto result = accountFacadeService.authenticate(request);

        // then
        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    @DisplayName("인증 - 존재하지 않는 계정이면 NOT_FOUND_ACCOUNT 예외를 던진다")
    void authenticate_whenAccountNotFound_throwsApplicationException() {
        // given
        TokenRequestDto request = TokenRequestDto.builder()
                .username("ghost")
                .password("pw12345")
                .build();
        given(accountDomainService.getOneByUsername("ghost")).willThrow(new AccountNotFoundDomainException());

        // when & then
        assertThatThrownBy(() -> accountFacadeService.authenticate(request))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Test
    @DisplayName("인증 - 비밀번호가 틀리면 NOT_FOUND_ACCOUNT 예외를 던진다")
    void authenticate_whenPasswordMismatch_throwsApplicationException() {
        // given
        TokenRequestDto request = TokenRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();
        Account account = activatedMemberAccount(1L, "testuser", "encodedPw");

        given(accountDomainService.getOneByUsername("testuser")).willReturn(account);
        given(accountDomainService.isMatchPassword("wrongpassword", "encodedPw")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> accountFacadeService.authenticate(request))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Test
    @DisplayName("인증 - 비활성화된 계정이면 ACCOUNT_DEACTIVATED 예외를 던진다")
    void authenticate_whenAccountDeactivated_throwsApplicationException() {
        // given
        TokenRequestDto request = TokenRequestDto.builder()
                .username("deactivated")
                .password("pw12345")
                .build();
        Account account = deactivatedMemberAccount(2L, "deactivated", "encodedPw");

        given(accountDomainService.getOneByUsername("deactivated")).willReturn(account);
        given(accountDomainService.isMatchPassword("pw12345", "encodedPw")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> accountFacadeService.authenticate(request))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.ACCOUNT_DEACTIVATED));
    }

    // ==================== refreshToken ====================

    @Test
    @DisplayName("토큰 갱신 - 유효한 리프레시 토큰이면 새 액세스 토큰을 반환한다 (리프레시 토큰 유효기간 충분)")
    void refreshToken_whenValidTokenWithSufficientExpiry_returnsNewAccessAndSameRefreshToken() {
        // given
        String rawRefreshToken = "valid-refresh-token";
        Jwt decodedJwt = buildJwt(rawRefreshToken, 7200); // 2시간 남음 (3600 이상)
        Account account = activatedMemberAccount(1L, "testuser", "encodedPw");
        Jwt newAccessJwt = buildJwt("new-access-token", 600);

        given(refreshTokenProvider.validateToken(rawRefreshToken)).willReturn(true);
        given(refreshTokenProvider.decodeFrom(rawRefreshToken)).willReturn(decodedJwt);
        given(refreshTokenProvider.getId(decodedJwt)).willReturn(1L);
        given(accountDomainService.getOneById(1L)).willReturn(account);
        given(refreshTokenProvider.getTokenWeight(decodedJwt)).willReturn(1L);
        given(accessTokenProvider.createToken(eq(1L), anySet())).willReturn(newAccessJwt);

        // when
        TokenResponseDto result = accountFacadeService.refreshToken(rawRefreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo(rawRefreshToken); // 동일한 리프레시 토큰 반환
        then(refreshTokenProvider).should(org.mockito.Mockito.never()).createToken(anyLong(), anyLong());
    }

    @Test
    @DisplayName("토큰 갱신 - 리프레시 토큰 유효기간이 1시간 미만이면 새 리프레시 토큰도 발급한다")
    void refreshToken_whenTokenExpiresWithinOneHour_returnsNewRefreshToken() {
        // given
        String rawRefreshToken = "soon-expiring-refresh-token";
        Jwt decodedJwt = buildJwt(rawRefreshToken, 1800); // 30분 남음 (3600 미만)
        Account account = activatedMemberAccount(1L, "testuser", "encodedPw");
        Jwt newAccessJwt = buildJwt("new-access-token", 600);
        Jwt newRefreshJwt = buildJwt("new-refresh-token", 86400);

        given(refreshTokenProvider.validateToken(rawRefreshToken)).willReturn(true);
        given(refreshTokenProvider.decodeFrom(rawRefreshToken)).willReturn(decodedJwt);
        given(refreshTokenProvider.getId(decodedJwt)).willReturn(1L);
        given(accountDomainService.getOneById(1L)).willReturn(account);
        given(refreshTokenProvider.getTokenWeight(decodedJwt)).willReturn(1L);
        given(accessTokenProvider.createToken(eq(1L), anySet())).willReturn(newAccessJwt);
        given(refreshTokenProvider.createToken(1L, 1L)).willReturn(newRefreshJwt);

        // when
        TokenResponseDto result = accountFacadeService.refreshToken(rawRefreshToken);

        // then
        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token"); // 새 리프레시 토큰 반환
    }

    @Test
    @DisplayName("토큰 갱신 - 유효하지 않은 리프레시 토큰이면 INVALID_REFRESH_TOKEN 예외를 던진다")
    void refreshToken_whenInvalidToken_throwsApplicationException() {
        // given
        String invalidToken = "invalid-refresh-token";
        given(refreshTokenProvider.validateToken(invalidToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> accountFacadeService.refreshToken(invalidToken))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.INVALID_REFRESH_TOKEN));
    }

    @Test
    @DisplayName("토큰 갱신 - 토큰에 해당하는 계정이 없으면 NOT_FOUND_ACCOUNT 예외를 던진다")
    void refreshToken_whenAccountNotFound_throwsApplicationException() {
        // given
        String rawRefreshToken = "valid-refresh-token";
        Jwt decodedJwt = buildJwt(rawRefreshToken, 7200);

        given(refreshTokenProvider.validateToken(rawRefreshToken)).willReturn(true);
        given(refreshTokenProvider.decodeFrom(rawRefreshToken)).willReturn(decodedJwt);
        given(refreshTokenProvider.getId(decodedJwt)).willReturn(999L);
        given(accountDomainService.getOneById(999L)).willThrow(new AccountNotFoundDomainException());

        // when & then
        assertThatThrownBy(() -> accountFacadeService.refreshToken(rawRefreshToken))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Test
    @DisplayName("토큰 갱신 - 비활성화된 계정이면 ACCOUNT_DEACTIVATED 예외를 던진다")
    void refreshToken_whenAccountDeactivated_throwsApplicationException() {
        // given
        String rawRefreshToken = "valid-refresh-token";
        Jwt decodedJwt = buildJwt(rawRefreshToken, 7200);
        Account deactivated = deactivatedMemberAccount(3L, "deactivated", "pw");

        given(refreshTokenProvider.validateToken(rawRefreshToken)).willReturn(true);
        given(refreshTokenProvider.decodeFrom(rawRefreshToken)).willReturn(decodedJwt);
        given(refreshTokenProvider.getId(decodedJwt)).willReturn(3L);
        given(accountDomainService.getOneById(3L)).willReturn(deactivated);

        // when & then
        assertThatThrownBy(() -> accountFacadeService.refreshToken(rawRefreshToken))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.ACCOUNT_DEACTIVATED));
    }

    @Test
    @DisplayName("토큰 갱신 - 계정 가중치가 토큰 가중치보다 크면 INVALID_REFRESH_TOKEN 예외를 던진다")
    void refreshToken_whenTokenWeightExceeded_throwsApplicationException() {
        // given
        String rawRefreshToken = "old-refresh-token";
        Jwt decodedJwt = buildJwt(rawRefreshToken, 7200);
        Account account = activatedMemberAccount(1L, "testuser", "encodedPw"); // tokenWeight=1

        given(refreshTokenProvider.validateToken(rawRefreshToken)).willReturn(true);
        given(refreshTokenProvider.decodeFrom(rawRefreshToken)).willReturn(decodedJwt);
        given(refreshTokenProvider.getId(decodedJwt)).willReturn(1L);
        given(accountDomainService.getOneById(1L)).willReturn(account);
        given(refreshTokenProvider.getTokenWeight(decodedJwt)).willReturn(0L); // 토큰 가중치 < 계정 가중치

        // when & then
        assertThatThrownBy(() -> accountFacadeService.refreshToken(rawRefreshToken))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.INVALID_REFRESH_TOKEN));
    }

    // ==================== invalidateRefreshTokenById ====================

    @Test
    @DisplayName("리프레시 토큰 무효화 - 존재하는 계정이면 토큰 가중치를 증가시킨다")
    void invalidateRefreshTokenById_whenAccountExists_increasesTokenWeight() {
        // given
        Long accountId = 1L;
        Account account = activatedMemberAccount(accountId, "testuser", "encodedPw");
        long originalWeight = account.getTokenWeight();
        given(accountDomainService.getOneById(accountId)).willReturn(account);

        // when
        accountFacadeService.invalidateRefreshTokenById(accountId);

        // then
        assertThat(account.getTokenWeight()).isEqualTo(originalWeight + 1);
    }

    @Test
    @DisplayName("리프레시 토큰 무효화 - 존재하지 않는 계정이면 NOT_FOUND_ACCOUNT 예외를 던진다")
    void invalidateRefreshTokenById_whenAccountNotFound_throwsApplicationException() {
        // given
        Long notExistId = 999L;
        given(accountDomainService.getOneById(notExistId)).willThrow(new AccountNotFoundDomainException());

        // when & then
        assertThatThrownBy(() -> accountFacadeService.invalidateRefreshTokenById(notExistId))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT));
    }

    // ==================== getAccountWithAuthorities ====================

    @Test
    @DisplayName("계정 정보 조회 - 존재하는 계정이면 AccountInfoResponseDto를 반환한다")
    void getAccountWithAuthorities_whenAccountExists_returnsAccountInfo() {
        // given
        Long accountId = 1L;
        Account account = activatedMemberAccount(accountId, "testuser", "encodedPw");
        given(accountDomainService.getOneById(accountId)).willReturn(account);

        // when
        AccountInfoResponseDto result = accountFacadeService.getAccountWithAuthorities(accountId);

        // then
        assertThat(result.username()).isEqualTo("testuser");
        assertThat(result.authoritySet()).contains("ROLE_MEMBER");
    }

    @Test
    @DisplayName("계정 정보 조회 - 존재하지 않는 계정이면 NOT_FOUND_ACCOUNT 예외를 던진다")
    void getAccountWithAuthorities_whenAccountNotFound_throwsApplicationException() {
        // given
        Long notExistId = 999L;
        given(accountDomainService.getOneById(notExistId)).willThrow(new AccountNotFoundDomainException());

        // when & then
        assertThatThrownBy(() -> accountFacadeService.getAccountWithAuthorities(notExistId))
                .isInstanceOf(ApplicationException.class)
                .satisfies(e -> assertThat(((ApplicationException) e).getErrorCode())
                        .isEqualTo(AccountErrorCode.NOT_FOUND_ACCOUNT));
    }

    // ==================== helpers ====================

    private Account activatedMemberAccount(Long id, String username, String encodedPw) {
        return Account.builder()
                .id(id)
                .username(username)
                .password(encodedPw)
                .nickname("닉네임")
                .authorities(Set.of(Authority.builder().authorityName("ROLE_MEMBER").build()))
                .activated(true)
                .build();
    }

    private Account deactivatedMemberAccount(Long id, String username, String encodedPw) {
        return Account.builder()
                .id(id)
                .username(username)
                .password(encodedPw)
                .nickname("비활성")
                .authorities(Set.of(Authority.builder().authorityName("ROLE_MEMBER").build()))
                .activated(false)
                .build();
    }

    private Jwt buildJwt(String tokenValue, long expiresInSeconds) {
        return Jwt.withTokenValue(tokenValue)
                .header("alg", "RS256")
                .subject("1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresInSeconds))
                .build();
    }
}
