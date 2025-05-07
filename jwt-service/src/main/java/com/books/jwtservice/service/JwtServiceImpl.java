package com.books.jwtservice.service;

import com.books.dto.JwtValidationResponse;
import com.books.dto.TokenRequest;
import com.books.dto.UserDto;
import com.books.jwtservice.client.UserFeignClient;
import com.books.utils.helper.RetryHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {
    private final ConsulServiceChecker consulServiceChecker;
    private final UserFeignClient userFeignClient;
    private final Key jwtSecret;

    public JwtServiceImpl(ConsulServiceChecker consulServiceChecker, UserFeignClient userFeignClient,
                          @Value("${JWT_SECRET:${jwt.secret}}") String secretKey) {
        this.consulServiceChecker = consulServiceChecker;
        this.userFeignClient = userFeignClient;
        jwtSecret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Override
    public String generateJwtToken(String username, HttpServletRequest request) {
        checkService(request);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 3600000)) // 1 hour
                .signWith(jwtSecret, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public JwtValidationResponse validateTokenAndGetUserInfo(TokenRequest tokenRequest, HttpServletRequest request) {
        checkService(request);
        String token = tokenRequest.getToken();
        UserDto user = RetryHelper.executeWithRetry(() -> userFeignClient.getUser(getUsernameFromJwtToken(token)));
        return new JwtValidationResponse(validateJwtToken(token), user.getUsername(), List.of(user.getRole()));
    }

    private void checkService(HttpServletRequest request) {
        String serviceName = request.getHeader("X-Service-Name");
        if (serviceName == null || !consulServiceChecker.isRegisteredService(serviceName)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Service " + serviceName + " is not registered.");
        }
    }

    private String getUsernameFromJwtToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    private boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            // Логирование или обработка исключений
            return false;
        }
    }
}
