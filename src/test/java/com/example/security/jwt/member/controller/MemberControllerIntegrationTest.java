package com.example.security.jwt.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
//@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com") // (1)
public class MemberControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유저 회원가입 테스트")
    void registerUserTest() throws Exception{
        Map<String, String> input = new HashMap<>();
        input.put("username", "member1");
        input.put("password", "member1");
        input.put("nickname", "member1_nickname");

        ResultActions actions = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.username", is(notNullValue())))
                .andExpect(jsonPath("$.response.password", is(notNullValue())))
                .andExpect(jsonPath("$.response.nickname", is(notNullValue())))
                .andExpect(jsonPath("$.response.tokenWeight", is(notNullValue())))
                .andExpect(jsonPath("$.response.authoritySet", is(notNullValue())))
                .andDo(print());
        // rest docs 문서화
        actions.andDo(document("member-register",
                requestFields(
                        fieldWithPath("username").description("회원가입 하고자 하는 username (USER_ROLE)"),
                        fieldWithPath("password").description("회원가입 하고자 하는 password"),
                        fieldWithPath("nickname").description("회원가입 하고자 하는 nickname")
                ),
                responseFields(
                        fieldWithPath("id").description("logging을 위한 api response 고유 ID"),
                        fieldWithPath("dateTime").description("response time"),
                        fieldWithPath("success").description("정상 응답 여부"),
                        fieldWithPath("response.username").description("회원가입 성공한 username"),
                        fieldWithPath("response.password").description("회원가입 성공한 password"),
                        fieldWithPath("response.nickname").description("회원가입 성공한 nickname"),
                        fieldWithPath("response.tokenWeight").description("초기 가중치"),
                        fieldWithPath("response.authoritySet").description("해당 사용자가 가진 권한"),
                        fieldWithPath("error").description("error 발생 시 에러 정보")
                )
        ));

    }
}
