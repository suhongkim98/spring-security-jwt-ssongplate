package com.example.security.jwt.admin.facade;

import com.example.security.jwt.admin.facade.dto.RequestAdminFacade;
import com.example.security.jwt.admin.facade.dto.ResponseAdminFacade;

public interface AdminFacade {

    ResponseAdminFacade.Information signup(RequestAdminFacade.Register registerDto);
}
