package com.books.user.service;

import com.books.dto.UserDto;
import com.books.user.model.dto.SignupRequest;

public interface UserService {
    void registerUser(SignupRequest signupRequest);

    String login(String username, String password);

    UserDto findUserByUsername(String username);
}
