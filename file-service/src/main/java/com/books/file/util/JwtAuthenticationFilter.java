package com.books.file.util;

import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import com.books.file.client.JwtFeignClient;
import com.books.utils.helper.RetryHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtFeignClient jwtFeignClient;

    @Autowired
    public JwtAuthenticationFilter(JwtFeignClient jwtFeignClient) {
        this.jwtFeignClient = jwtFeignClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) {
                JwtValidationResponse validationResponse = RetryHelper
                        .executeWithRetry(() -> jwtFeignClient.validateJwtToken(new TokenRequest(jwt)));  // Отправляем токен на проверку

                if (validationResponse.isValid()) {
                    UserDetails userDetails = new User(
                            validationResponse.getUsername(),
                            "",
                            validationResponse.getAuthorities().stream().map(SimpleGrantedAuthority::new)
                                    .toList()
                    );

                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, jwt,
                            userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("User was not authenticated : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);  // Извлекаем токен, убирая "Bearer "
        }

        return null;
    }
}
