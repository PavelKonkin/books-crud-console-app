package com.books.jwtservice.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsulFeignConfig {
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${spring.cloud.consul.discovery.acl-token}")
    private String consulToken;

    @Bean
    public RequestInterceptor consulRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Consul-Token", consulToken);
            requestTemplate.header("X-Service-Name", applicationName);
        };
    }
}
