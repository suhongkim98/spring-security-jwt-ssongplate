package com.example.security.jwt.admin.application;

import com.example.security.jwt.admin.application.dto.RequestAdmin;
import com.example.security.jwt.admin.application.dto.ResponseAdmin;

public interface AdminService {
    ResponseAdmin.Info signup(RequestAdmin.Register registerDto);
}
