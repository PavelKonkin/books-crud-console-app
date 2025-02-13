package com.books.book.util;

import com.books.book.client.JwtFeignClient;
import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
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
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtFeignClient jwtFeignClient;  // Feign клиент для общения с jwt сервисом

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);  // Получаем токен из заголовка
            if (jwt != null) {
                JwtValidationResponse validationResponse = jwtFeignClient.validateJwtToken(new TokenRequest(jwt));  // Отправляем токен на проверку

                if (validationResponse.isValid()) {  // Проверка валидности
                    UserDetails userDetails = new User(
                            validationResponse.getUsername(),
                            "",
                            validationResponse.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );

                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            log.error("Не удалось аутентифицировать пользователя: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);  // Передаем управление следующему фильтру
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);  // Извлекаем токен, убирая "Bearer "
        }

        return null;
    }
}
