package com.books.user.util;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        return Objects.equals(secret, ((JwtServiceAuthenticationToken) o).secret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), secret);
    }
}
