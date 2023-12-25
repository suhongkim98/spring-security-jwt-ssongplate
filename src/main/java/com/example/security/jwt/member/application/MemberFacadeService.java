package com.example.security.jwt.member.application;

import com.example.security.jwt.member.application.dto.RequestMemberFacade;
import com.example.security.jwt.member.application.dto.ResponseMemberFacade;

public interface MemberFacadeService {

    ResponseMemberFacade.Information signup(RequestMemberFacade.Register registerDto);
}
