package com.books.user.service;

import com.books.dto.UserDto;
import com.books.exception.NotFoundException;
import com.books.user.client.JwtFeignClient;
import com.books.user.model.User;
import com.books.user.model.dto.SignupRequest;
import com.books.user.model.dto.UserRole;
import com.books.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtFeignClient jwtFeignClient;
    private final MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtFeignClient jwtFeignClient, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtFeignClient = jwtFeignClient;
        this.messageSource = messageSource;
    }

    public void registerUser(SignupRequest signupRequest) {
        User user = new User(signupRequest.getUsername(), signupRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        userRepository.save(user);
    }

    public String login(String username, String password) {
        // Ищем пользователя по имени
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(messageSource
                        .getMessage("userNotFoundWithUsername", null,
                                LocaleContextHolder.getLocale()) + username));

        // Проверяем, совпадает ли введенный пароль с сохраненным (используем BCryptPasswordEncoder)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(messageSource
                    .getMessage("invalidCredentials", null,
                            LocaleContextHolder.getLocale()));
        }

        // Если аутентификация прошла успешно, запрашиваем токен у jwt-service

        return jwtFeignClient.generateJwtToken(username);
    }

    @Override
    public UserDto findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(messageSource
                        .getMessage("userNotFoundWithUsername", null,
                                LocaleContextHolder.getLocale()) + username));
        return new UserDto(user.getUsername(), user.getRole().name());
    }
}
