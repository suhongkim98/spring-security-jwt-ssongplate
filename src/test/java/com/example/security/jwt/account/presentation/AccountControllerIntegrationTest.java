package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.AccountFacadeService;
import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.account.domain.AccountDomainService;
import com.example.security.jwt.account.domain.entity.Account;
import com.example.security.jwt.member.application.MemberFacadeService;
import com.example.security.jwt.member.application.dto.RegisterMemberFacadeRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountControllerIntegrationTest {

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
    void setUp() {
        memberFacadeService.signup(RegisterMemberFacadeRequestDto.builder()
                .username("acc_member").password("memberpassword").nickname("멤버").build());
        accountDomainService.createAdminAccount("acc_admin", "adminpassword", "관리자");
        accountDomainService.createUserAccount("acc_inactive", "inactivepass", "비활성화예정");
    }

    // ==================== POST /api/v1/accounts/token ====================

    @Test
    @DisplayName("[통합] 로그인 - 유효한 자격증명이면 200과 토큰 및 쿠키를 반환한다")
    void authorize_whenValidCredentials_returns200WithTokensAndCookie() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "acc_member",
                "password", "memberpassword"
        );

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", notNullValue()))
                .andExpect(jsonPath("$.response.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.response.accessTokenExpireAt", notNullValue()))
                .andExpect(jsonPath("$.response.refreshTokenExpireAt", notNullValue()))
                .andExpect(cookie().exists("RT"))
                .andExpect(cookie().httpOnly("RT", true))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 로그인 - 비밀번호가 틀리면 404를 반환한다")
    void authorize_whenWrongPassword_returns404() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "acc_member",
                "password", "wrongpassword"
        );

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 로그인 - 존재하지 않는 username이면 404를 반환한다")
    void authorize_whenAccountNotFound_returns404() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "nobody",
                "password", "somepassword"
        );

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 로그인 - username이 3자 미만이면 validation 실패로 400을 반환한다")
    void authorize_whenUsernameTooShort_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of(
                "username", "ab",
                "password", "somepassword"
        );

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_002"))
                .andDo(print());
    }

    // ==================== PUT /api/v1/accounts/token ====================

    @Test
    @DisplayName("[통합] 토큰 갱신 - 유효한 리프레시 토큰이면 200과 새 액세스 토큰을 반환한다")
    void refreshToken_whenValidRefreshToken_returns200WithNewAccessToken() throws Exception {
        // given
        TokenResponseDto token = accountFacadeService.authenticate(TokenRequestDto.builder()
                .username("acc_member").password("memberpassword").build());

        // when & then
        mockMvc.perform(put("/api/v1/accounts/token")
                        .header("RT", token.refreshToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken", notNullValue()))
                .andExpect(jsonPath("$.response.refreshToken", notNullValue()))
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 토큰 갱신 - 유효하지 않은 리프레시 토큰이면 400을 반환한다")
    void refreshToken_whenInvalidRefreshToken_returns400() throws Exception {
        // given
        String invalidToken = "invalid.refresh.token";

        // when & then
        mockMvc.perform(put("/api/v1/accounts/token")
                        .header("RT", invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_001"))
                .andDo(print());
    }

    // ==================== DELETE /api/v1/accounts/{accountId}/token ====================

    @Test
    @DisplayName("[통합] 리프레시 토큰 무효화 - ADMIN이 요청하면 204를 반환하고 토큰 가중치가 증가한다")
    void deleteRefreshToken_whenAdminRole_returns204AndIncreasesTokenWeight() throws Exception {
        // given
        accountDomainService.createUserAccount("acc_del_target", "targetpassword", "삭제대상");
        Account target = accountDomainService.getOneByUsername("acc_del_target");
        long originalWeight = target.getTokenWeight();

        TokenResponseDto adminToken = accountFacadeService.authenticate(TokenRequestDto.builder()
                .username("acc_admin").password("adminpassword").build());

        // when
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", target.getId())
                        .header("Authorization", "Bearer " + adminToken.accessToken()))
                .andExpect(status().isNoContent())
                .andDo(print());

        // then
        Account updated = accountDomainService.getOneById(target.getId());
        org.assertj.core.api.Assertions.assertThat(updated.getTokenWeight()).isEqualTo(originalWeight + 1);
    }

    @Test
    @DisplayName("[통합] 리프레시 토큰 무효화 - MEMBER 권한으로 요청하면 403을 반환한다")
    void deleteRefreshToken_whenMemberRole_returns403() throws Exception {
        // given
        TokenResponseDto memberToken = accountFacadeService.authenticate(TokenRequestDto.builder()
                .username("acc_member").password("memberpassword").build());

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 999L)
                        .header("Authorization", "Bearer " + memberToken.accessToken()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 리프레시 토큰 무효화 - 인증 없이 요청하면 401을 반환한다")
    void deleteRefreshToken_whenUnauthenticated_returns401() throws Exception {
        // given (인증 헤더 없음)

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 1L))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("[통합] 리프레시 토큰 무효화 - 존재하지 않는 계정 ID이면 404를 반환한다")
    void deleteRefreshToken_whenAccountNotFound_returns404() throws Exception {
        // given
        TokenResponseDto adminToken = accountFacadeService.authenticate(TokenRequestDto.builder()
                .username("acc_admin").password("adminpassword").build());

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 99999L)
                        .header("Authorization", "Bearer " + adminToken.accessToken()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_002"))
                .andDo(print());
    }
}
