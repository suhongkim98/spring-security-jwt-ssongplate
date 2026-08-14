package com.example.security.jwt.account.domain;

import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.account.domain.exception.AccountConflictDomainException;
import com.example.security.jwt.account.domain.exception.AccountNotFoundDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AccountDomainServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountDomainService accountDomainService;

    // ==================== getOneById ====================

    @Test
    @DisplayName("ID로 계정 조회 - 존재하는 계정이면 Account를 반환한다")
    void getOneById_whenAccountExists_returnsAccount() {
        // given
        Long accountId = 1L;
        Account account = Account.builder()
                .id(accountId)
                .username("testuser")
                .password("encodedPw")
                .nickname("테스터")
                .authorities(Set.of(Authority.builder().authorityName("ROLE_MEMBER").build()))
                .activated(true)
                .build();
        given(accountRepository.findOneWithAuthoritiesById(accountId)).willReturn(Optional.of(account));

        // when
        Account result = accountDomainService.getOneById(accountId);

        // then
        assertThat(result.getId()).isEqualTo(accountId);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("ID로 계정 조회 - 존재하지 않으면 AccountNotFoundDomainException을 던진다")
    void getOneById_whenAccountNotExists_throwsAccountNotFoundDomainException() {
        // given
        Long notExistId = 999L;
        given(accountRepository.findOneWithAuthoritiesById(notExistId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountDomainService.getOneById(notExistId))
                .isInstanceOf(AccountNotFoundDomainException.class);
    }

    // ==================== getOneByUsername ====================

    @Test
    @DisplayName("username으로 계정 조회 - 존재하는 계정이면 Account를 반환한다")
    void getOneByUsername_whenAccountExists_returnsAccount() {
        // given
        String username = "testuser";
        Account account = Account.builder()
                .id(1L)
                .username(username)
                .password("encodedPw")
                .nickname("테스터")
                .authorities(Set.of())
                .activated(true)
                .build();
        given(accountRepository.findOneWithAuthoritiesByUsername(username)).willReturn(Optional.of(account));

        // when
        Account result = accountDomainService.getOneByUsername(username);

        // then
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("username으로 계정 조회 - 존재하지 않으면 AccountNotFoundDomainException을 던진다")
    void getOneByUsername_whenAccountNotExists_throwsAccountNotFoundDomainException() {
        // given
        String notExistUsername = "ghost";
        given(accountRepository.findOneWithAuthoritiesByUsername(notExistUsername)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountDomainService.getOneByUsername(notExistUsername))
                .isInstanceOf(AccountNotFoundDomainException.class);
    }

    // ==================== createUserAccount ====================

    @Test
    @DisplayName("일반 유저 계정 생성 - username이 중복되지 않으면 계정을 저장한다")
    void createUserAccount_whenUsernameNotDuplicated_savesAccount() {
        // given
        String username = "newuser";
        String password = "rawPassword";
        String nickname = "닉네임";
        given(accountRepository.findOneWithAuthoritiesByUsername(username)).willReturn(Optional.empty());
        given(passwordEncoder.encode(password)).willReturn("encodedPassword");

        // when
        accountDomainService.createUserAccount(username, password, nickname);

        // then
        then(accountRepository).should().save(any(Account.class));
    }

    @Test
    @DisplayName("일반 유저 계정 생성 - username이 중복이면 AccountConflictDomainException을 던진다")
    void createUserAccount_whenUsernameDuplicated_throwsAccountConflictDomainException() {
        // given
        String duplicatedUsername = "existing";
        Account existing = Account.builder()
                .id(1L).username(duplicatedUsername).password("pw").nickname("nn")
                .authorities(Set.of()).activated(true).build();
        given(accountRepository.findOneWithAuthoritiesByUsername(duplicatedUsername)).willReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> accountDomainService.createUserAccount(duplicatedUsername, "pw", "nn"))
                .isInstanceOf(AccountConflictDomainException.class);
        then(accountRepository).should(never()).save(any());
    }

    // ==================== createAdminAccount ====================

    @Test
    @DisplayName("관리자 계정 생성 - username이 중복되지 않으면 계정을 저장한다")
    void createAdminAccount_whenUsernameNotDuplicated_savesAccount() {
        // given
        String username = "newadmin";
        String password = "adminPw";
        String nickname = "관리자";
        given(accountRepository.findOneWithAuthoritiesByUsername(username)).willReturn(Optional.empty());
        given(passwordEncoder.encode(password)).willReturn("encodedAdminPw");

        // when
        accountDomainService.createAdminAccount(username, password, nickname);

        // then
        then(accountRepository).should().save(any(Account.class));
    }

    @Test
    @DisplayName("관리자 계정 생성 - username이 중복이면 AccountConflictDomainException을 던진다")
    void createAdminAccount_whenUsernameDuplicated_throwsAccountConflictDomainException() {
        // given
        String duplicatedUsername = "existingAdmin";
        Account existing = Account.builder()
                .id(2L).username(duplicatedUsername).password("pw").nickname("nn")
                .authorities(Set.of()).activated(true).build();
        given(accountRepository.findOneWithAuthoritiesByUsername(duplicatedUsername)).willReturn(Optional.of(existing));

        // when & then
        assertThatThrownBy(() -> accountDomainService.createAdminAccount(duplicatedUsername, "pw", "nn"))
                .isInstanceOf(AccountConflictDomainException.class);
        then(accountRepository).should(never()).save(any());
    }

    // ==================== isMatchPassword ====================

    @Test
    @DisplayName("비밀번호 일치 확인 - 일치하면 true를 반환한다")
    void isMatchPassword_whenPasswordMatches_returnsTrue() {
        // given
        String rawPassword = "mypassword";
        String encodedPassword = "encodedHash";
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

        // when
        boolean result = accountDomainService.isMatchPassword(rawPassword, encodedPassword);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 불일치하면 false를 반환한다")
    void isMatchPassword_whenPasswordNotMatches_returnsFalse() {
        // given
        String rawPassword = "wrongpassword";
        String encodedPassword = "encodedHash";
        given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

        // when
        boolean result = accountDomainService.isMatchPassword(rawPassword, encodedPassword);

        // then
        assertThat(result).isFalse();
    }
}
