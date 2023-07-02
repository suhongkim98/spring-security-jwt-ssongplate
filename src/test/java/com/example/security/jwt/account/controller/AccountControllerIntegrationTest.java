package com.example.security.jwt.account.controller;

import com.example.security.jwt.admin.dto.RequestAdmin;
import com.example.security.jwt.admin.service.AdminService;
import com.example.security.jwt.account.service.AccountService;
import com.example.security.jwt.member.dto.RequestMember;
import com.example.security.jwt.member.service.MemberService;
import com.example.security.jwt.account.dto.ResponseAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
//@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com") // (1)
public class AccountControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AdminService adminService;

    @BeforeAll
    void beforeAll() {
        // 회원 생성
        memberService.signup(RequestMember.Register.builder()
                        .nickname("길동이")
                        .username("gildong")
                        .password("gildongspassword")
                .build());
        memberService.signup(RequestMember.Register.builder()
                .nickname("두식이")
                .username("dusik")
                .password("dusikpassword")
                .build());
        adminService.signup(RequestAdmin.Register.builder()
                        .nickname("나야어드민")
                        .username("honghong")
                        .password("hongpassword")
                .build());
    }

    @Test
    @DisplayName("사용자 인증 테스트")
    void authenticateTest() throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("username", "dusik");
        input.put("password", "dusikpassword");

        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())))
                .andDo(print());

        // rest docs 문서화
        actions.andDo(document("account-authenticate",
                requestFields(
                        fieldWithPath("username").description("인증 하고자 하는 username"),
                        fieldWithPath("password").description("인증 하고자 하는 password")
                ),
                responseFields(
                        fieldWithPath("id").description("logging을 위한 api response 고유 ID"),
                        fieldWithPath("dateTime").description("response time"),
                        fieldWithPath("success").description("정상 응답 여부"),
                        fieldWithPath("response.accessToken").description("액세스 토큰"),
                        fieldWithPath("response.refreshToken").description("리프레시 토큰"),
                        fieldWithPath("error").description("error 발생 시 에러 정보")
                )
        ));
    }

    @Test
    @DisplayName("액세스 토큰 갱신 테스트")
    void refreshTokenTest() throws Exception {
        // given
        ResponseAccount.Token token = accountService.authenticate("dusik", "dusikpassword");
        Map<String, Object> input = new HashMap<>();
        input.put("token", token.refreshToken());

        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())))
                .andDo(print());
        // rest docs 문서화
        actions.andDo(document("account-token-refresh",
                requestFields(
                        fieldWithPath("token").description("액세스 토큰 갱신에 사용되는 리프레시 토큰")
                ),
                responseFields(
                        fieldWithPath("id").description("logging을 위한 api response 고유 ID"),
                        fieldWithPath("dateTime").description("response time"),
                        fieldWithPath("success").description("정상 응답 여부"),
                        fieldWithPath("response.accessToken").description("액세스 토큰"),
                        fieldWithPath("response.refreshToken").description("리프레시 토큰"),
                        fieldWithPath("error").description("error 발생 시 에러 정보")
                )
        ));

    }
    @Test
    @DisplayName("관리자의 사용자 리프레시 토큰 만료 테스트")
    void invalidateRefreshTokenTest() throws Exception {
        // given
        ResponseAccount.Token token = accountService.authenticate("honghong", "hongpassword");
        String targetUsername = "dusik"; // 두식이 계정 토큰 만료시키기

        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/accounts/{username}/token", targetUsername)
                        .header("Authorization", "Bearer " + token.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", "true").exists())
                .andDo(print());

        // rest docs 문서화
        actions.andDo(document("account-token-invalidate",
                pathParameters(
                        parameterWithName("username").description("만료시키고자 하는 계정 username")
                ),
                responseFields(
                        fieldWithPath("id").description("logging을 위한 api response 고유 ID"),
                        fieldWithPath("dateTime").description("response time"),
                        fieldWithPath("success").description("정상 응답 여부"),
                        fieldWithPath("response").description("null"),
                        fieldWithPath("error").description("error 발생 시 에러 정보")
                )
        ));

    }
}
