package com.example.security.jwt.member.application;

import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;

public interface MemberFacadeService {

    void signup(RegisterMemberFacadeRequestDto requestDto);
}
