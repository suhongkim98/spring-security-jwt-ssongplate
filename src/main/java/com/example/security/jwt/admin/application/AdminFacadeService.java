package com.example.security.jwt.admin.application;

import com.example.security.jwt.admin.application.dto.RequestAdminFacade;
import com.example.security.jwt.admin.application.dto.ResponseAdminFacade;

public interface AdminFacadeService {

    ResponseAdminFacade.Information signup(RequestAdminFacade.Register registerDto);
}
