package com.example.security.jwt.user.service;

// 회원가입, 유저정보 조회

import com.example.security.jwt.user.dto.RequestUser;
import com.example.security.jwt.user.dto.ResponseUser;
import com.example.security.jwt.auth.domain.Account;
import com.example.security.jwt.auth.domain.Authority;
import com.example.security.jwt.global.exception.error.DuplicateMemberException;
import com.example.security.jwt.auth.repository.AccountRepository;
import com.example.security.jwt.global.security.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

// USER 권한을 가진 회원 관련 로직
@Service
public class UserServiceImpl implements UserService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 메서드
    @Transactional
    @Override
    public ResponseUser.Info signup(RequestUser.Register registerDto) {
        if (accountRepository.findOneWithAuthoritiesByUsername(registerDto.getUsername()).orElseGet(()->null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 이 유저는 권한이 ROLE_USER
        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        Account user = Account.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .nickname(registerDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // DB에 저장하고 그걸 DTO로 변환해서 반환, 예제라서 비번까지 다 보낸다. 원랜 당연히 보내면 안댐
        return ResponseUser.Info.of(accountRepository.save(user));
    }

    // username을 파라미터로 받아 해당 유저의 정보를 가져온다
    @Transactional(readOnly = true)
    @Override
    public ResponseUser.Info getUserWithAuthorities(String username) {
        return ResponseUser.Info.of(accountRepository.findOneWithAuthoritiesByUsername(username).orElseGet(()->null));
    }

    // 현재 시큐리티 컨텍스트에 저장된 username에 해당하는 정보를 가져온다.
    @Transactional(readOnly = true)
    @Override
    public ResponseUser.Info getMyUserWithAuthorities() {
        return ResponseUser.Info.of(SecurityUtil.getCurrentUsername().flatMap(accountRepository::findOneWithAuthoritiesByUsername).orElseGet(()->null));
    }
}