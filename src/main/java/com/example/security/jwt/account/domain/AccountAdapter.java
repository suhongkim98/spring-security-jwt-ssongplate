package com.example.security.jwt.account.domain;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// 인증 API는 그 과정에서 loadUserByUsername를 호출해 디비에서 사용자 정보를 꺼내온다.
// 인증 API 호출 시 엔티티 유저를 반환하여 사용하고 싶어 어댑터 패턴 사용
// authentication.getPrincipal() 로 AccountAdapter 가져올 수 있음
public class AccountAdapter extends User {
    private Account account;

    public AccountAdapter(Account account) {
        super(account.getUsername(), account.getPassword(), authorities(account.getAuthorities()));
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

    private static List<GrantedAuthority> authorities(Set<Authority> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
    }
}
