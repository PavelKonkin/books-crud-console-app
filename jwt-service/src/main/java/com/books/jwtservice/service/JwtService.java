package com.books.jwtservice.service;

import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String generateJwtToken(String username, HttpServletRequest request);

    JwtValidationResponse validateTokenAndGetUserInfo(TokenRequest tokenRequest, HttpServletRequest request);
}
