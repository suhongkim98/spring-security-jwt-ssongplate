package com.example.security.jwt.account.presentation;

import com.example.security.jwt.account.application.dto.TokenRequestDto;
import com.example.security.jwt.account.application.dto.TokenResponseDto;
import com.example.security.jwt.global.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Account", description = "계정 API")
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "400",
                description = "BAD_REQUEST",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "500",
                description = "INTERNAL_SERVER_ERROR",
                content = @Content
        ),
})
public interface AccountApi {

    @Operation(tags = {"Account"}, summary = "Account 인증", description = """
            ## API 설명
            username, password 를 이용해 계정을 인증합니다.
            
            * 해당 사용자 권한으로 액세스토큰 리프레시 토큰 반환
            * 리프레시 토큰 Http Only, Secure 쿠키 추가
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"),
    })
    ResponseEntity<CommonResponse<TokenResponseDto>> authorize(TokenRequestDto tokenRequestDto);

    @Operation(tags = {"Account"}, summary = "토큰 갱신", description = """
            ## API 설명
            Refresh Token을 이용하여 액세스 토큰을 갱신합니다.
            요청 시 리프레시 토큰을 정해진 헤더값에 넣어 호출해줍니다.
            
            * 리프레시 토큰을 검증(유효성, 리프레시 토큰 가중치)
            * 액세스 토큰 발급
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"),
    })
    ResponseEntity<CommonResponse<TokenResponseDto>> refreshToken(String refreshToken);

    @Operation(tags = {"Account"}, summary = "Refresh Token 만료", description = """
            ## API 설명
            토큰 버저닝 방식으로 해당 accountId의 토큰 가중치를 1 증가시킴으로써 이전에 발급된 해당 accountId에 대한 모든 리프레시 토큰 무효화합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "NO_CONTENT"),
    })
    @SecurityRequirement(name = "bearer-key")
    ResponseEntity<Void> deleteRefreshToken(Long accountId);
}
