package com.books.jwtservice.config;

import com.books.jwtservice.config.errordecoder.FeignErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserFeignConfig {
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${jwt.service.secret}")
    private String jwtSecret;

    @Bean
    public RequestInterceptor userRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Service-Name", applicationName);
            requestTemplate.header("X-JWT-Service-Secret", jwtSecret);
        };
    }

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }
}
