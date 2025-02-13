package com.books.user.util;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtServiceAuthenticationToken extends AbstractAuthenticationToken {

    private final String secret;

    public JwtServiceAuthenticationToken(String secret, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.secret = secret;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return secret;
    }
}
