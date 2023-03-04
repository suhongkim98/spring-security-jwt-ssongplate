package com.example.security.jwt.member.service;

import com.example.security.jwt.member.dto.RequestMember;
import com.example.security.jwt.member.dto.ResponseMember;

public interface MemberService {
    ResponseMember.Info signup(RequestMember.Register registerDto);
    ResponseMember.Info getUserWithAuthorities(String username);
    ResponseMember.Info getMyUserWithAuthorities();
}
