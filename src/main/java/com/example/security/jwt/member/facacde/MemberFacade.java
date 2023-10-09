package com.example.security.jwt.member.facacde;

import com.example.security.jwt.member.facacde.dto.RequestMemberFacade;
import com.example.security.jwt.member.facacde.dto.ResponseMemberFacade;

public interface MemberFacade {

    ResponseMemberFacade.Information signup(RequestMemberFacade.Register registerDto);
}
