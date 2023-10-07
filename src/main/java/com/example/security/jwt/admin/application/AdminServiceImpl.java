package com.example.security.jwt.admin.application;

import com.example.security.jwt.admin.application.dto.RequestAdmin;
import com.example.security.jwt.admin.application.dto.ResponseAdmin;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import com.example.security.jwt.global.exception.CommonErrorCode;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.account.domain.AccountRepository;
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
            throw new ApplicationException(CommonErrorCode.CONFLICT);
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
