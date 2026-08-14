package com.example.security.jwt.member.presentation;

import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberFacadeService memberFacadeService;

    // ==================== POST /api/v1/members ====================

    @Test
    @DisplayName("[통합] 회원가입 - 유효한 요청이면 204를 반환한다")
    void signup_whenValidRequest_returns204() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "mem_newuser",
                "password", "password123",
                "nickname", "새회원닉네임"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 회원가입 - username이 중복이면 409를 반환한다")
    void signup_whenUsernameDuplicated_returns409() throws Exception {
        // given
        String duplicatedUsername = "mem_dupuser";
        memberFacadeService.signup(RegisterMemberFacadeRequestDto.builder()
                .username(duplicatedUsername)
                .password("password123")
                .nickname("기존회원")
                .build());

        Map<String, String> body = Map.of(
                "username", duplicatedUsername,
                "password", "anotherpassword",
                "nickname", "또다른닉네임"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("MEMBER_001"))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 회원가입 - username이 3자 미만이면 400을 반환한다")
    void signup_whenUsernameTooShort_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "ab",
                "password", "password123",
                "nickname", "닉네임"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 회원가입 - password가 5자 미만이면 400을 반환한다")
    void signup_whenPasswordTooShort_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "validuser",
                "password", "abc",
                "nickname", "닉네임"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 회원가입 - nickname이 누락되면 400을 반환한다")
    void signup_whenNicknameIsMissing_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "validuser2",
                "password", "password123"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
