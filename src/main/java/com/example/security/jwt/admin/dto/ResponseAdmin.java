package com.example.security.jwt.admin.dto;

import com.example.security.jwt.account.domain.Account;
import com.example.security.jwt.account.domain.Authority;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

public record ResponseAdmin() {
    @Builder
    public record Info(
            String username,
            String password,
            String nickname,
            Long tokenWeight,
            Set<String> authoritySet
    ) {

        public static ResponseAdmin.Info of(Account account) {
            if(account == null) return null;

            return ResponseAdmin.Info.builder()
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .nickname(account.getNickname())
                    .tokenWeight(account.getTokenWeight())
                    .authoritySet(account.getAuthorities().stream()
                            .map(Authority::getAuthorityName)
                            .collect(Collectors.toSet()))
                    .build();
        }
    }
}
