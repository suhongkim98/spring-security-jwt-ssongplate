package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.AccountErrorCode;
import com.example.security.jwt.account.application.AccountFacadeService;
import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.global.exception.ApplicationException;
import com.example.security.jwt.global.security.jwt.CustomAccountPrincipal;
import com.example.security.jwt.global.security.jwt.CustomJwtAuthenticationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@org.springframework.test.context.TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=none"
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountFacadeService accountFacadeService;

    // ==================== POST /api/v1/accounts/token ====================

    @Test
    @DisplayName("로그인 - 유효한 자격증명이면 200과 토큰을 반환하고 RT 쿠키를 설정한다")
    void authorize_whenValidCredentials_returns200WithTokensAndCookie() throws Exception {
        // given
        TokenResponseDto token = TokenResponseDto.builder()
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .accessTokenExpireAt(Instant.now().plusSeconds(600))
                .refreshTokenExpireAt(Instant.now().plusSeconds(86400))
                .build();
        given(accountFacadeService.authenticate(any(TokenRequestDto.class))).willReturn(token);
        Map<String, String> body = Map.of("username", "testuser", "password", "testpassword");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.response.refreshToken").value("mock-refresh-token"))
                .andExpect(cookie().exists("RT"))
                .andExpect(cookie().httpOnly("RT", true))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - username이 null이면 400을 반환한다")
    void authorize_whenUsernameIsNull_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of("password", "testpassword");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - username이 3자 미만이면 400을 반환한다")
    void authorize_whenUsernameTooShort_returns400() throws Exception {
        // given
        Map<String, String> body = Map.of("username", "ab", "password", "testpassword");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("COMMON_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 계정이면 404를 반환한다")
    void authorize_whenAccountNotFound_returns404() throws Exception {
        // given
        given(accountFacadeService.authenticate(any(TokenRequestDto.class)))
                .willThrow(new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT));
        Map<String, String> body = Map.of("username", "unknown", "password", "testpassword");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_002"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 - 비활성화된 계정이면 403을 반환한다")
    void authorize_whenAccountDeactivated_returns403() throws Exception {
        // given
        given(accountFacadeService.authenticate(any(TokenRequestDto.class)))
                .willThrow(new ApplicationException(AccountErrorCode.ACCOUNT_DEACTIVATED));
        Map<String, String> body = Map.of("username", "deactivated", "password", "testpassword");

        // when & then
        mockMvc.perform(post("/api/v1/accounts/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_003"))
                .andDo(print());
    }

    // ==================== PUT /api/v1/accounts/token ====================

    @Test
    @DisplayName("토큰 갱신 - 유효한 리프레시 토큰이면 200과 새 토큰을 반환한다")
    void refreshToken_whenValidRefreshToken_returns200WithNewTokens() throws Exception {
        // given
        TokenResponseDto newToken = TokenResponseDto.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .accessTokenExpireAt(Instant.now().plusSeconds(600))
                .refreshTokenExpireAt(Instant.now().plusSeconds(86400))
                .build();
        given(accountFacadeService.refreshToken("valid-refresh-token")).willReturn(newToken);

        // when & then
        mockMvc.perform(put("/api/v1/accounts/token")
                        .header("RT", "valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.response.refreshToken").value("new-refresh-token"))
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 갱신 - 유효하지 않은 리프레시 토큰이면 400을 반환한다")
    void refreshToken_whenInvalidRefreshToken_returns400() throws Exception {
        // given
        given(accountFacadeService.refreshToken("invalid-refresh-token"))
                .willThrow(new ApplicationException(AccountErrorCode.INVALID_REFRESH_TOKEN));

        // when & then
        mockMvc.perform(put("/api/v1/accounts/token")
                        .header("RT", "invalid-refresh-token"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_001"))
                .andDo(print());
    }

    // ==================== DELETE /api/v1/accounts/{accountId}/token ====================

    @Test
    @DisplayName("리프레시 토큰 무효화 - ADMIN 권한이면 204를 반환한다")
    void deleteRefreshToken_whenAdminRole_returns204() throws Exception {
        // given
        willDoNothing().given(accountFacadeService).invalidateRefreshTokenById(2L);

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 2L)
                        .with(adminAuth()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("리프레시 토큰 무효화 - MEMBER 권한이면 403을 반환한다")
    void deleteRefreshToken_whenMemberRole_returns403() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 2L)
                        .with(memberAuth()))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("리프레시 토큰 무효화 - 인증 없이 요청하면 401을 반환한다")
    void deleteRefreshToken_whenUnauthenticated_returns401() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 2L))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("리프레시 토큰 무효화 - ADMIN이 존재하지 않는 계정 ID를 요청하면 404를 반환한다")
    void deleteRefreshToken_whenAdminButAccountNotFound_returns404() throws Exception {
        // given
        willThrow(new ApplicationException(AccountErrorCode.NOT_FOUND_ACCOUNT))
                .given(accountFacadeService).invalidateRefreshTokenById(99999L);

        // when & then
        mockMvc.perform(delete("/api/v1/accounts/{accountId}/token", 99999L)
                        .with(adminAuth()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("ACCOUNT_002"))
                .andDo(print());
    }

    // ==================== helpers ====================

    private RequestPostProcessor adminAuth() {
        Jwt jwt = Jwt.withTokenValue("admin-token")
                .header("alg", "none")
                .subject("1")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();
        CustomJwtAuthenticationToken auth = new CustomJwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_ADMIN"),
                        new SimpleGrantedAuthority("SCOPE_ROLE_MEMBER")),
                new CustomAccountPrincipal("1")
        );
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }

    private RequestPostProcessor memberAuth() {
        Jwt jwt = Jwt.withTokenValue("member-token")
                .header("alg", "none")
                .subject("2")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600))
                .build();
        CustomJwtAuthenticationToken auth = new CustomJwtAuthenticationToken(
                jwt,
                List.of(new SimpleGrantedAuthority("SCOPE_ROLE_MEMBER")),
                new CustomAccountPrincipal("2")
        );
        return SecurityMockMvcRequestPostProcessors.authentication(auth);
    }
}
