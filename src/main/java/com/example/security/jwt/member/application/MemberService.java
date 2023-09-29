package com.example.security.jwt.member.application;

import com.example.security.jwt.member.application.dto.RequestMember;
import com.example.security.jwt.member.application.dto.ResponseMember;

public interface MemberService {
    ResponseMember.Info signup(RequestMember.Register registerDto);
    ResponseMember.Info getUserWithAuthorities(String username);
    ResponseMember.Info getMyUserWithAuthorities();
}
