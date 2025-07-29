package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.account.application.AccountFacadeService;
import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import com.example.security.jwt.member.application.MemberFacadeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberFacadeService memberFacadeService;
    @Autowired
    private AccountFacadeService accountFacadeService;
    @Autowired
    private AccountDomainService accountDomainService;

    @BeforeAll
    void beforeAll() {
        // 회원 생성
        memberFacadeService.signup(RegisterMemberFacadeRequestDto.builder()
                        .nickname("길동이")
                        .username("gildong")
                        .password("gildongspassword")
                .build());
        memberFacadeService.signup(RegisterMemberFacadeRequestDto.builder()
                .nickname("두식이")
                .username("dusik")
                .password("dusikpassword")
                .build());
        accountDomainService.createAdminAccount("honghong", "hongpassword", "나야어드민");
    }

    @Test
    @DisplayName("사용자 인증 테스트")
    void authenticateTest() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("username", "dusik");
        input.put("password", "dusikpassword");

        ResultActions actions = mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())))
                .andDo(print());
    }

    @Test
    @DisplayName("액세스 토큰 갱신 테스트")
    void refreshTokenTest() throws Exception {
        // given
        TokenResponseDto token = accountFacadeService.authenticate(TokenRequestDto.builder()
                        .username("dusik")
                        .password("dusikpassword")
                .build());
        Map<String, Object> input = new HashMap<>();
        input.put("token", token.refreshToken());

        ResultActions actions = mockMvc.perform(put("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())))
                .andDo(print());

    }
    @Test
    @DisplayName("관리자의 사용자 리프레시 토큰 만료 테스트")
    void invalidateRefreshTokenTest() throws Exception {
        // given
        TokenResponseDto token = accountFacadeService.authenticate(TokenRequestDto.builder()
                        .username("honghong")
                        .password("hongpassword")
                .build());
        String targetUsername = "dusik"; // 두식이 계정 토큰 만료시키기

        ResultActions actions = mockMvc.perform(delete("/api/v1/accounts/{username}/token", targetUsername)
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
