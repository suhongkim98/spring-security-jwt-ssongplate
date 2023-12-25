package com.example.security.jwt.member.application.dto;

import lombok.Builder;

import java.util.Set;

public record ResponseMemberFacade() {
    @Builder
    public record Information(
            String username,
            String password,
            String nickname,
            Long tokenWeight,
            Set<String> authoritySet
    ) {
    }
}
