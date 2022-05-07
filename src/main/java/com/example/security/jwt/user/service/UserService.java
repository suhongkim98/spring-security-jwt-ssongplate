package com.example.security.jwt.user.service;

import com.example.security.jwt.user.dto.RequestUser;
import com.example.security.jwt.user.dto.ResponseUser;

public interface UserService {
    ResponseUser.Info signup(RequestUser.Register registerDto);
    ResponseUser.Info getUserWithAuthorities(String username);
    ResponseUser.Info getMyUserWithAuthorities();
}
