package com.books.consul.envpostprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@Slf4j
public class ConsulTokenEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String pathToFile;
        if (System.getenv("CONSUL_HOST") != null) {
            pathToFile = "consul/data/";
        } else if (Files.exists(Paths.get("consul_data/service_token.txt"))) {
            pathToFile = "consul_data/";
        } else {
            pathToFile = "../consul_data/";
        }
        try {
            // Читаем токен из файла, путь должен быть доступен в контейнере
            String token = Files.readString(Paths.get(pathToFile + "service_token.txt")).trim();
            environment.getPropertySources().addFirst(
                    new MapPropertySource("consulTokenSource", Collections.singletonMap("spring.cloud.consul.discovery.acl-token", token))
            );
        } catch (IOException e) {
            // Если токен не найден залогировать предупреждение
            log.info("Consul token was unable to read from " + pathToFile + e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
