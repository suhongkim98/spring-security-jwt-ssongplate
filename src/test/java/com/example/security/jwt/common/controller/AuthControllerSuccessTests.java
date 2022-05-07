package com.example.security.jwt.common.controller;

import com.example.security.jwt.admin.dto.RequestAdmin;
import com.example.security.jwt.admin.service.AdminService;
import com.example.security.jwt.common.service.AuthService;
import com.example.security.jwt.user.dto.RequestUser;
import com.example.security.jwt.common.dto.ResponseAuth;
import com.example.security.jwt.user.service.UserService;
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

@ActiveProfiles("local")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
//@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com") // (1)
public class AuthControllerSuccessTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private AdminService adminService;

    @BeforeAll
    void beforeAll() {
        // 회원 생성
        userService.signup(RequestUser.Register.builder()
                        .nickname("길동이")
                        .username("gildong")
                        .password("gildongspassword")
                .build());
        userService.signup(RequestUser.Register.builder()
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

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andDo(print())
                // rest docs 문서화
                .andDo(document("account-authenticate",
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
                ))
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())));
    }

    @Test
    @DisplayName("액세스 토큰 갱신 테스트")
    void refreshTokenTest() throws Exception {
        // given
        ResponseAuth.Token token = authService.authenticate("dusik", "dusikpassword");
        Map<String, Object> input = new HashMap<>();
        input.put("token", token.getRefreshToken());

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andDo(print())
                // rest docs 문서화
                .andDo(document("account-token-refresh",
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
                ))
                .andExpect(jsonPath("$.response.accessToken", is(notNullValue())))
                .andExpect(jsonPath("$.response.refreshToken", is(notNullValue())));
    }
    @Test
    @DisplayName("관리자의 사용자 리프레시 토큰 만료 테스트")
    void invalidateRefreshTokenTest() throws Exception {
        // given
        ResponseAuth.Token token = authService.authenticate("honghong", "hongpassword");
        String targetUsername = "dusik"; // 두식이 계정 토큰 만료시키기

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/token/refresh/{username}", targetUsername)
                        .header("Authorization", "bearer " + token.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                // rest docs 문서화
                .andDo(document("account-token-invalidate",
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
                ))
                .andExpect(jsonPath("$.success", "true").exists());
    }
}