package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtValidationResponse {
    private boolean valid;          // Флаг валидности токена
    private String username;        // Имя пользователя, извлеченное из токена
    private List<String> authorities; // Роли пользователя
}

