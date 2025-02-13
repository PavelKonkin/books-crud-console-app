package com.books.file.client;

import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtFeignClientFallback implements JwtFeignClient {
    @Override
    public JwtValidationResponse validateJwtToken(TokenRequest tokenRequest) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "JWT service is unavailable.");
    }
}
