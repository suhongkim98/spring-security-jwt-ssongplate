package com.example.security.jwt.admin.application;

import com.example.security.jwt.admin.application.dto.RegisterAdminFacadeRequestDto;

public interface AdminFacadeService {

    void signup(RegisterAdminFacadeRequestDto requestDto);
}
