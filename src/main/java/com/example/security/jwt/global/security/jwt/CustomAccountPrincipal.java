package com.example.security.jwt.global.security.jwt;

import lombok.Getter;

@Getter
public class CustomAccountPrincipal {

    private final String accountId;

    public CustomAccountPrincipal(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String toString() {
        return accountId;
    }
}
