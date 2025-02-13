package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequest {
    private String token;

    public TokenRequest(String token) {
        this.token = token;
    }
}

