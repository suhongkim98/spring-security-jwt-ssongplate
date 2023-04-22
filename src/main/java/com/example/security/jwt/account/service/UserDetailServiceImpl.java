package com.example.security.jwt.account.service;

import com.example.security.jwt.account.domain.Account;
import com.example.security.jwt.account.domain.AccountAdapter;
import com.example.security.jwt.account.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    public UserDetailServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // "인증 API 호출 시에만" 그 과정에서 재정의한 loadUserByUsername를 호출하여 디비에서 유저정보와 권한정보를 가져온다.
    // 내가 만든 커스텀 UserAdapter 는 org.springframework.security.core.userdetails.User 를 상속받았으므로 이걸 반환해도 된다.
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));

        if(!account.isActivated()) throw new RuntimeException(account.getUsername() + " -> 활성화되어 있지 않습니다.");
        return new AccountAdapter(account);
    }
}
