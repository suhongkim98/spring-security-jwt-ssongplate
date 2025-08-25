package com.example.security.jwt.global.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final CustomJwtGrantedAuthoritiesConverter jwtRoleConverter;

    public CustomJwtAuthenticationConverter(CustomJwtGrantedAuthoritiesConverter jwtRoleConverter) {
        this.jwtRoleConverter = jwtRoleConverter;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = jwtRoleConverter.convert(jwt);

        CustomAccountPrincipal principal = new CustomAccountPrincipal(
                jwt.getSubject()
        );

        CustomJwtAuthenticationToken token = new CustomJwtAuthenticationToken(jwt, authorities, principal);
        return token;
    }
}
