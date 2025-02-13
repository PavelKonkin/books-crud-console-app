package com.books.user.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtServiceAuthFilter extends OncePerRequestFilter {

    private final String jwtServiceSecret;

    public JwtServiceAuthFilter(String jwtServiceSecret) {
        this.jwtServiceSecret = jwtServiceSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String secretHeader = request.getHeader("X-JWT-Service-Secret");

        if (secretHeader != null && secretHeader.equals(jwtServiceSecret)) {
            // Если заголовок верен, устанавливаем аутентификацию с авторитетом JWT_SERVICE
            JwtServiceAuthenticationToken auth = new JwtServiceAuthenticationToken(jwtServiceSecret,
                    Collections.singletonList(new SimpleGrantedAuthority("JWT_SERVICE")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
