package com.example.security.jwt.member.presentation;

import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.member.application.MemberErrorCode;
import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=none"
})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberFacadeService memberFacadeService;

    // ==================== POST /api/v1/members ====================

    @Test
    @DisplayName("회원가입 - 유효한 요청이면 204를 반환한다")
    void signup_whenValidRequest_returns204() throws Exception {
        // given
        willDoNothing().given(memberFacadeService).signup(any(RegisterMemberFacadeRequestDto.class));
        Map<String, String> body = Map.of(
                "username", "newmember",
                "password", "password123",
                "nickname", "새로운회원"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 - username이 null이면 400을 반환한다")
    void signup_whenUsernameIsNull_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of(
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
    @DisplayName("회원가입 - username이 3자 미만이면 400을 반환한다")
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
    @DisplayName("회원가입 - password가 5자 미만이면 400을 반환한다")
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
    @DisplayName("회원가입 - nickname이 누락되면 400을 반환한다")
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

    @Test
    @DisplayName("회원가입 - username이 중복이면 409를 반환한다")
    void signup_whenUsernameDuplicated_returns409() throws Exception {
        // given
        willThrow(new ApplicationException(MemberErrorCode.CONFLICT_MEMBER_ACCOUNT))
                .given(memberFacadeService).signup(any(RegisterMemberFacadeRequestDto.class));
        Map<String, String> body = Map.of(
                "username", "existing",
                "password", "password123",
                "nickname", "중복닉네임"
        );

        // when & then
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("MEMBER_001"))
                .andDo(print());
    }
}
