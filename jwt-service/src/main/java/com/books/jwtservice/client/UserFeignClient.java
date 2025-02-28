package com.books.jwtservice.client;

import com.books.dto.UserDto;
import com.books.jwtservice.config.UserFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", configuration = UserFeignConfig.class,
        fallback = UserFeignClientFallback.class)
@Primary
public interface UserFeignClient {
    @GetMapping("/api/v1/users")
    UserDto getUser(@RequestParam("username") String username);
}
