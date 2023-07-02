package com.example.security.jwt.admin.service;

import com.example.security.jwt.admin.dto.RequestAdmin;
import com.example.security.jwt.admin.dto.ResponseAdmin;
import com.example.security.jwt.account.domain.Account;
import com.example.security.jwt.account.domain.Authority;
import com.example.security.jwt.global.exception.error.DuplicateMemberException;
import com.example.security.jwt.account.repository.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


// ADMIN 권한
@Service
public class AdminServiceImpl implements AdminService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 메서드
    @Transactional
    @Override
    public ResponseAdmin.Info signup(RequestAdmin.Register registerDto) {
        if (accountRepository.findOneWithAuthoritiesByUsername(registerDto.username()).orElseGet(()->null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 권한이 ROLE_ADMIN
        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_ADMIN")
                .build();

        Account member = Account.builder()
                .username(registerDto.username())
                .password(passwordEncoder.encode(registerDto.password()))
                .nickname(registerDto.nickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // DB에 저장하고 그걸 DTO로 변환해서 반환, 예제라서 비번까지 다 보낸다. 원랜 당연히 보내면 안댐
        return ResponseAdmin.Info.of(accountRepository.save(member));
    }
}
