package com.books.jwtservice.controller;

import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import com.books.jwtservice.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jwt")
@Slf4j
public class JwtController {
    private final JwtService jwtService;

    @Autowired
    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/generate-token")
    public String generateJwtToken(@RequestParam("username") String username, HttpServletRequest request) {
        return jwtService.generateJwtToken(username, request);
    }

    @PostMapping("/validate-token")
    public JwtValidationResponse validateJwtToken(@RequestBody TokenRequest tokenRequest, HttpServletRequest request) {
        return jwtService.validateTokenAndGetUserInfo(tokenRequest, request);
    }
}
