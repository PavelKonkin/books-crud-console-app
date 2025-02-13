package com.books.user.service;

import com.books.dto.UserDto;
import com.books.user.model.dto.SignupRequest;

public interface UserService {
    public void registerUser(SignupRequest signupRequest);

    public String login(String username, String password);

    UserDto findUserByUsername(String username);
}
