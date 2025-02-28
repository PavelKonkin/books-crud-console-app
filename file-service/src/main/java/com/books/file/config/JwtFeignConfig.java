package com.books.file.config;

import com.books.file.config.errordecoder.FeignErrorDecoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtFeignConfig {
    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public RequestInterceptor jwtRequestInterceptor() {
        return requestTemplate -> requestTemplate.header("X-Service-Name", applicationName);
    }

    @Bean
    public ErrorDecoder jwtErrorDecoder(ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }
}