package com.books.jwtservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.books.jwtservice", "com.books.exception"})
@EnableDiscoveryClient
@EnableFeignClients
public class JwtServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwtServiceApplication.class, args);
    }
}