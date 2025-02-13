package com.books.consul.envpostprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class ConsulTokenEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String pathToFile = System.getenv("CONSUL_HOST") == null ? "consul_data/" : "consul/data/";
        try {
            // Читаем токен из файла, путь должен быть доступен в контейнере
            String token = Files.readString(Paths.get(pathToFile + "service_token.txt")).trim();
            environment.getPropertySources().addFirst(
                    new MapPropertySource("consulTokenSource", Collections.singletonMap("spring.cloud.consul.discovery.acl-token", token))
            );
//            System.out.println("Bootstrap token установлен: " + token);
        } catch (IOException e) {
            // Если токен не найден, можно залогировать предупреждение или выбросить исключение
            System.err.println("Не удалось прочитать токен из /consul/data/service_token.txt: " + e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
