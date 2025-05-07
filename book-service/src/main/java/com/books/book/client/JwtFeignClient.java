package com.books.book.client;

import com.books.book.config.FeignConfig;
import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "jwt-service", configuration = FeignConfig.class, fallback = JwtFeignClientFallback.class)
@Primary
public interface JwtFeignClient {
    @PostMapping("/api/v1/jwt/validate-token")
    JwtValidationResponse validateJwtToken(@RequestBody TokenRequest tokenRequest);
}
