package com.books.jwtservice.client;

import com.books.jwtservice.model.dto.ConsulRegisteredService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class ConsulFeignClientFallback implements ConsulFeignClient {
    @Override
    public List<ConsulRegisteredService> getService(String serviceName) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Consul service is unavailable.");
    }
}
