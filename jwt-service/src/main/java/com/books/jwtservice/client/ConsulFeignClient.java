package com.books.jwtservice.client;

import com.books.jwtservice.config.ConsulFeignConfig;
import com.books.jwtservice.model.dto.ConsulRegisteredService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "consulClient", configuration = ConsulFeignConfig.class, url = "http://${CONSUL_HOST:localhost}:8500",
        fallback = ConsulFeignClientFallback.class)
@Primary
public interface ConsulFeignClient {
    @GetMapping("/v1/catalog/service/{serviceName}")
    List<ConsulRegisteredService> getService(@PathVariable("serviceName") String serviceName);
}
