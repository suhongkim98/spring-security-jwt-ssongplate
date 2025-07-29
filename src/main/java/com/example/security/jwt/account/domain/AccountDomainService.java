package com.example.security.jwt.account.domain;

import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.account.domain.exception.AccountConflictDomainException;
import com.example.security.jwt.account.domain.exception.AccountNotFoundDomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountDomainService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account getOneById(String username) {
        return findOneById(username).orElseThrow(AccountNotFoundDomainException::new);
    }

    public Optional<Account> findOneById(String username) {
        return accountRepository.findOneWithAuthoritiesByUsername(username);
    }

    public void createUserAccount(String username, String password, String nickname) {
        Optional<Account> accountOptional = findOneById(username);

        if (accountOptional.isPresent()) {
            throw new AccountConflictDomainException();
        }

        // 이 유저는 권한이 MEMBER
        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_MEMBER")
                .build();

        Account account = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();
        accountRepository.save(account);
    }

    public void createAdminAccount(String username, String password, String nickname) {
        Optional<Account> accountOptional = findOneById(username);

        if (accountOptional.isPresent()) {
            throw new AccountConflictDomainException();
        }

        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_ADMIN")
                .build();

        Account user = Account.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        accountRepository.save(user);
    }

    public boolean isMatchPassword(String password, String encodePassword) {
        return passwordEncoder.matches(password, encodePassword);
    }
}
