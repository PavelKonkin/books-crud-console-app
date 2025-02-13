package com.books.user.client;

import com.books.user.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "jwt-service", configuration = FeignConfig.class, fallback = JwtFeignClientFallback.class)
@Primary
public interface JwtFeignClient {
    @PostMapping("api/v1/jwt/generate-token")
    String generateJwtToken(@RequestParam("username") String username);
}
