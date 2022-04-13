package com.example.security.jwt.service;

// 회원가입, 유저정보 조회

import com.example.security.jwt.controller.dto.RequestUser;
import com.example.security.jwt.controller.dto.ResponseUser;
import com.example.security.jwt.entity.Authority;
import com.example.security.jwt.entity.User;
import com.example.security.jwt.exception.error.DuplicateMemberException;
import com.example.security.jwt.repository.UserRepository;
import com.example.security.jwt.security.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 메서드
    @Transactional
    public ResponseUser.Info signup(RequestUser.Register registerDto) {
        if (userRepository.findOneWithAuthoritiesByUsername(registerDto.getUsername()).orElseGet(()->null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // 이 유저는 권한이 ROLE_USER
        // 이건 부팅 시 data.sql에서 INSERT로 디비에 반영한다. 즉 디비에 존재하는 값이여야함
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .nickname(registerDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // DB에 저장하고 그걸 DTO로 변환해서 반환, 예제라서 비번까지 다 보낸다. 원랜 당연히 보내면 안댐
        return ResponseUser.Info.of(userRepository.save(user));
    }

    // username을 파라미터로 받아 해당 유저의 정보를 가져온다
    @Transactional(readOnly = true)
    public ResponseUser.Info getUserWithAuthorities(String username) {
        return ResponseUser.Info.of(userRepository.findOneWithAuthoritiesByUsername(username).orElseGet(()->null));
    }

    // 현재 시큐리티 컨텍스트에 저장된 username에 해당하는 정보를 가져온다.
    @Transactional(readOnly = true)
    public ResponseUser.Info getMyUserWithAuthorities() {
        return ResponseUser.Info.of(SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername).orElseGet(()->null));
    }
}