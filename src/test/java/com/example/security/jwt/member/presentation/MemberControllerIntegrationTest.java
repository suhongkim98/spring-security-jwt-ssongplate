package com.example.security.jwt.member.presentation;

import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberFacadeService memberFacadeService;

    @Test
    @DisplayName("유저 회원가입 테스트")
    void registerUserTest() throws Exception{
        Map<String, String> input = new HashMap<>();
        input.put("username", "member1");
        input.put("password", "member1");
        input.put("nickname", "member1_nickname");

        ResultActions actions = mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("유저 회원가입 중복 테스트")
    void registerDuplicateUserTest() throws Exception{
        // given
        String username = "dup";
        String password = "dusikpassworddup";
        String nickname = "두식이dup";
        memberFacadeService.signup(RegisterMemberFacadeRequestDto.builder()
                .nickname(nickname)
                .username(username)
                .password(password)
                .build());

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);
        input.put("nickname", nickname);

        // when then
        ResultActions actions = mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}
