package com.example.security.jwt.global.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    private final CustomAccountPrincipal principal;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, CustomAccountPrincipal principal) {
        super(jwt, authorities, principal.getAccountId());
        this.principal = principal;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
