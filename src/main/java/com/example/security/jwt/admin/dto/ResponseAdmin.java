package com.example.security.jwt.admin.dto;

import com.example.security.jwt.auth.domain.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

public class ResponseAdmin {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String username;
        private String password;
        private String nickname;
        private Long tokenWeight;
        private Set<String> authoritySet;

        public static ResponseAdmin.Info of(Account account) {
            if(account == null) return null;

            return ResponseAdmin.Info.builder()
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .nickname(account.getNickname())
                    .tokenWeight(account.getTokenWeight())
                    .authoritySet(account.getAuthorities().stream()
                            .map(authority -> authority.getAuthorityName())
                            .collect(Collectors.toSet()))
                    .build();
        }
    }
}
