package com.example.security.jwt.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

public class ResponseUser {
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private String username;
        private String password;
        private String nickname;
        private Set<String> authoritySet;

        public static ResponseUser.Info of(com.example.security.jwt.entity.User user) {
            if(user == null) return null;

            return ResponseUser.Info.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .nickname(user.getNickname())
                    .authoritySet(user.getAuthorities().stream()
                            .map(authority -> authority.getAuthorityName())
                            .collect(Collectors.toSet()))
                    .build();
        }
    }
}
