package com.books.jwtservice.service;

import com.books.jwtservice.client.ConsulFeignClient;
import com.books.jwtservice.model.dto.ConsulRegisteredService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsulServiceCheckerImpl implements ConsulServiceChecker {
    private final ConsulFeignClient consulClient;

    @Value("${CONSUL_SERVER_ROLE:server}")
    private String requiredTag;

    public ConsulServiceCheckerImpl(ConsulFeignClient consulClient) {
        this.consulClient = consulClient;
    }

    public boolean isRegisteredService(String serviceName) {
        List<ConsulRegisteredService> services = consulClient.getService(serviceName);

        return services.stream()
                .anyMatch(service -> service.getServiceTags().contains(requiredTag));
    }
}
