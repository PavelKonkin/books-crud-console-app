package com.books.user.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtFeignClientFallback implements JwtFeignClient {
    @Override
    public String generateJwtToken(String username) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "JWT service is unavailable.");
    }
}
