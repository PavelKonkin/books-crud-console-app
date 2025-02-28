package com.books.jwtservice.client;

import com.books.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserFeignClientFallback implements UserFeignClient {
    @Override
    public UserDto getUser(String username) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Consul service is unavailable.");
    }
}
