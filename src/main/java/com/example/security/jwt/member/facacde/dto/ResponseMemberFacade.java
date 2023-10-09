package com.example.security.jwt.member.facacde.dto;

import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.account.domain.entity.Authority;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

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
