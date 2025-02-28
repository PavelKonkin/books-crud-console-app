package com.books.user.controller;


import com.books.dto.UserDto;
import com.books.user.model.dto.LoginRequest;
import com.books.user.model.dto.SignupRequest;
import com.books.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;

    @Autowired
    public UserController(UserService userService, MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        log.info(messageSource
                .getMessage("registerUserRequestMessage", null,
                        LocaleContextHolder.getLocale()), signupRequest);
        userService.registerUser(signupRequest);
        log.info(messageSource
                .getMessage("registerUserSuccessMessage", null,
                        LocaleContextHolder.getLocale()), signupRequest);
        return ResponseEntity.ok(messageSource
                .getMessage("registerUserSuccessResponseMessage", null,
                        LocaleContextHolder.getLocale()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info(messageSource
                .getMessage("loginUserRequestMessage", null,
                        LocaleContextHolder.getLocale()), loginRequest);
        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        log.info(messageSource
                .getMessage("loginUserSuccessMessage", null,
                        LocaleContextHolder.getLocale()), loginRequest);
        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,
                "Bearer " + token).body(messageSource
                .getMessage("loginUserSuccessResponseMessage", null,
                        LocaleContextHolder.getLocale()));
    }

    @PreAuthorize("hasAuthority('JWT_SERVICE')")
    @GetMapping
    UserDto getUser(@RequestParam("username") String username) {
        return userService.findUserByUsername(username);
    }
}
