package com.example.security.jwt.admin.service;

import com.example.security.jwt.admin.dto.RequestAdmin;
import com.example.security.jwt.admin.dto.ResponseAdmin;

public interface AdminService {
    ResponseAdmin.Info signup(RequestAdmin.Register registerDto);
}
