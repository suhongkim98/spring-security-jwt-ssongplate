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
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"),
    })
    ResponseEntity<CommonResponse<TokenResponseDto>> authorize(TokenRequestDto tokenRequestDto);

    @Operation(tags = {"Account"}, summary = "액세스 토큰 갱신", description = """
            ## API 설명
            Refresh Token을 이용하여 액세스 토큰을 갱신합니다.
            요청 시 리프레시 토큰을 정해진 헤더값에 넣어 호출해줍니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK"),
    })
    ResponseEntity<CommonResponse<TokenResponseDto>> refreshToken(String refreshToken);

    @Operation(tags = {"Account"}, summary = "Refresh Token 만료", description = """
            ## API 설명
            토큰 버저닝 방식으로 해당 유저의 Refresh Token을 만료 시킵니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "NO_CONTENT"),
    })
    @SecurityRequirement(name = "bearer-key")
    ResponseEntity<Void> deleteRefreshToken(Long accountId);
}
