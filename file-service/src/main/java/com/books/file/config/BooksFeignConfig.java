package com.books.file.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class BooksFeignConfig {
    @Bean
    public RequestInterceptor booksRequestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getCredentials() != null) {
                String jwt = (String) authentication.getCredentials();  // Извлекаем токен
                requestTemplate.header("Authorization", "Bearer " + jwt);  // Передаем токен в заголовке
            }
        };
    }
}